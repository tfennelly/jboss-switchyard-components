/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.component.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.ServiceDomain;
import org.switchyard.contract.DefaultExchangeContract;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.metadata.ServiceOperation;

/**
 * Client Proxy CDI Bean.
 * <p/>
 * CDI bean for injecting into consumer beans where the {@link org.switchyard.component.bean.Reference @Reference}
 * is used.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClientProxyBean implements Bean {

    /**
     * The target Service {@link QName}.
     */
    private QName serviceQName;

    /**
     * The bean proxy Interface {@link Class} of the bean being proxied.  This class
     * must be one of the {@link org.switchyard.component.bean.Service @Service}
     * interfaces implemented by the actual Service bean component.
     */
    private Class<?> proxyInterface;

    /**
     * CDI bean qualifiers.  See CDI Specification.
     */
    private Set<Annotation> qualifiers;

    /**
     * The dynamic proxy bean instance created from the supplied {@link #proxyInterface}.
     */
    private Object proxyBean;

    /**
     * Public constructor.
     *
     * @param serviceQName   The name of the ESB Service being proxied to.
     * @param proxyInterface The proxy Interface.
     * @param qualifiers     The CDI bean qualifiers.  Copied from the injection point.
     */
    public ClientProxyBean(QName serviceQName, Class<?> proxyInterface, Set<Annotation> qualifiers) {
        this.serviceQName = serviceQName;
        this.proxyInterface = proxyInterface;

        if (qualifiers != null) {
            this.qualifiers = qualifiers;
        } else {
            this.qualifiers = new HashSet<Annotation>();
            this.qualifiers.add(new AnnotationLiteral<Default>() {
            });
            this.qualifiers.add(new AnnotationLiteral<Any>() {
            });
        }

        proxyBean = Proxy.newProxyInstance(ClientProxyBean.class.getClassLoader(),
                new Class[]{proxyInterface},
                new ClientProxyInvocationHandler());
    }

    /**
     * Get the name of the ESB Service being proxied to.
     *
     * @return The Service name.
     */
    public QName getServiceQName() {
        return serviceQName;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject bean types} of the bean.
     *
     * @return the {@linkplain javax.enterprise.inject bean types}
     */
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(proxyInterface);
        types.add(Object.class);
        return types;
    }

    /**
     * Obtains the {@linkplain javax.inject.Qualifier qualifiers} of the bean.
     *
     * @return the {@linkplain javax.inject.Qualifier qualifiers}
     */
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject EL name} of a bean, if it has one.
     *
     * @return the {@linkplain javax.enterprise.inject EL name}
     */
    public String getName() {
        // TODO: Can we take this from the Bean instance associated with the actual service... think that may cause a duplicate bean name bean resolution issue
        return null;
    }

    /**
     * Obtains the {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     * of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     */
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    /**
     * The bean {@linkplain Class class} of the managed bean or session bean or
     * of the bean that declares the producer method or field.
     *
     * @return the bean {@linkplain Class class}
     */
    public Class<?> getBeanClass() {
        return proxyInterface;
    }

    /**
     * Determines if the bean is an
     * {@linkplain javax.enterprise.inject.Alternative alternative}.
     *
     * @return <tt>true</tt> if the bean is an
     *         {@linkplain javax.enterprise.inject.Alternative alternative},
     *         and <tt>false</tt> otherwise.
     */
    public boolean isAlternative() {
        return false;
    }

    /**
     * Determines if
     * {@link javax.enterprise.context.spi.Contextual#create(CreationalContext)}
     * sometimes return a null value.
     *
     * @return <tt>true</tt> if the {@code create()} method may return a null
     *         value, and <tt>false</tt> otherwise
     */
    public boolean isNullable() {
        return false;
    }

    /**
     * Obtains the {@link javax.enterprise.inject.spi.InjectionPoint} objects
     * representing injection points of the bean, that will be validated by the
     * container at initialization time.
     *
     * @return the set of {@linkplain javax.enterprise.inject.spi.InjectionPoint injection points} of the bean
     */
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    /**
     * Obtains the {@linkplain javax.enterprise.context scope} of the bean.
     *
     * @return the {@linkplain javax.enterprise.context scope}
     */
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    /**
     * Create a new instance of the contextual type. Instances should
     * use the given {@link javax.enterprise.context.spi.CreationalContext}
     * when obtaining contextual references to inject, in order to ensure
     * that any dependent objects are associated with the contextual instance
     * that is being created. An implementation may call
     * {@link javax.enterprise.context.spi.CreationalContext#push(Object)}
     * between instantiation and injection to help the container minimize the
     * use of client proxy objects.
     *
     * @param creationalContext the context in which this instance is being created
     * @return the contextual instance
     * @throws javax.enterprise.inject.CreationException
     *          if a checked exception occurs while creating the instance
     */
    public Object create(CreationalContext creationalContext) {
        return proxyBean;
    }

    /**
     * Destroy an instance of the contextual type. Implementations should
     * call {@link javax.enterprise.context.spi.CreationalContext#release()}
     * to allow the container to destroy dependent objects of the contextual
     * instance.
     *
     * @param instance          the contextual instance to destroy
     * @param creationalContext the context in which this instance was created
     */
    public void destroy(Object instance, CreationalContext creationalContext) {

    }

    /**
     * Dynamic proxy {@link InvocationHandler}.
     */
    private class ClientProxyInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ServiceDomain domain = ServiceDomains.getDomain();
            org.switchyard.Service service = domain.getService(serviceQName);

            if (service == null) {
                throw new BeanComponentException("Service not registered: " + serviceQName);
            }

            if (method.getReturnType() != null && !Void.TYPE.isAssignableFrom(method.getReturnType())) {
                final BlockingQueue<Exchange> responseQueue = new ArrayBlockingQueue<Exchange>(1);

                ExchangeHandler responseExchangeHandler = new ExchangeHandler() {
                    public void handleMessage(Exchange exchange) throws HandlerException {
                        responseQueue.offer(exchange);
                    }

                    public void handleFault(Exchange exchange) {
                        responseQueue.offer(exchange);
                    }
                };


                Exchange exchangeIn = createExchange(domain, service, method, responseExchangeHandler);
                exchangeIn.send(exchangeIn.createMessage().setContent(args));

                Exchange exchangeOut = responseQueue.take();
                if(exchangeOut.getState() == ExchangeState.OK) {
                    return exchangeOut.getMessage().getContent();
                } else {
                    Object exceptionObj = exchangeOut.getMessage().getContent();

                    if(exceptionObj instanceof Throwable) {
                        if(exceptionObj instanceof BeanComponentException) {
                            BeanComponentException beanCompException = (BeanComponentException) exceptionObj;
                            Throwable cause = beanCompException.getCause();
                            if(cause instanceof InvocationTargetException) {
                                throw cause.getCause();
                            } else {
                                throw cause;
                            }
                        }
                        throw (Throwable) exceptionObj;
                    } else {
                        throw new BeanComponentException("Bean Component invocation failure.  Service '" + serviceQName + "', operation '" + method.getName() + "'.").setFaultExchange(exchangeOut);
                    }
                }
            } else {
                Exchange exchange = createExchange(domain, service, method, null);
                exchange.send(exchange.createMessage().setContent(args));

                return null;
            }
        }

        private Exchange createExchange(ServiceDomain domain, org.switchyard.Service service, Method method, ExchangeHandler responseExchangeHandler) throws BeanComponentException {
            String operationName = method.getName();
            ServiceOperation operation = service.getInterface().getOperation(operationName);

            if(operation == null) {
                throw new BeanComponentException("Bean Component invocation failure.  Operation '" + operationName + "' is not defined on Service '" + serviceQName + "'.");
            }

            return domain.createExchange(service, new DefaultExchangeContract(operation), responseExchangeHandler);
        }

    }
}
