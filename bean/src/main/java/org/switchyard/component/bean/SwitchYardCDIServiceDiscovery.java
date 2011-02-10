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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.xml.namespace.QName;

import org.switchyard.component.bean.deploy.ApplicationServiceDescriptorSet;
import org.switchyard.component.bean.deploy.CDIBeanServiceDescriptor;
import org.switchyard.internal.ServiceDomains;
import org.switchyard.transform.Transformer;

/**
 * Portable CDI extension for SwitchYard.
 * <p/>
 * See the {@link #afterBeanDiscovery(javax.enterprise.inject.spi.AfterBeanDiscovery, javax.enterprise.inject.spi.BeanManager) afterBeanDiscovery}
 * method.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIServiceDiscovery implements Extension {

    /**
     * List of created {@link ClientProxyBean} instances.
     */
    private List<ClientProxyBean> _createdProxyBeans = new ArrayList<ClientProxyBean>();

    /**
     * {@link javax.enterprise.inject.spi.AfterBeanDiscovery} CDI event observer.
     * <p/>
     * Responsible for the following:
     * <ol>
     * <li>Creates and registers (with the CDI {@link BeanManager Bean Manager}) all the {@link ClientProxyBean}
     * instances for all {@link org.switchyard.component.bean.Reference} injection points.</li>
     * </ol>
     *
     * @param abd         CDI Event instance.
     * @param beanManager CDI Bean Manager instance.
     */
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        ApplicationServiceDescriptorSet appDescriptorSet = ApplicationServiceDescriptorSet.lookup();
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {
        });

        for (Bean<?> bean : allBeans) {
            Set<InjectionPoint> injectionPoints = bean.getInjectionPoints();

            // Create proxies for the relevant injection points...
            for (InjectionPoint injectionPoint : injectionPoints) {
                for (Annotation qualifier : injectionPoint.getQualifiers()) {
                    if (Reference.class.isAssignableFrom(qualifier.annotationType())) {
                        Member member = injectionPoint.getMember();
                        if (member instanceof Field) {
                            Class<?> memberType = ((Field) member).getType();
                            if (memberType.isInterface()) {
                                addInjectableClientProxyBean((Field) member, (Reference) qualifier, injectionPoint.getQualifiers(), beanManager, abd);
                            }
                        }
                    }
                }
            }

            // Create Service Proxy ExchangeHandlers and register them as Services, for all @Service beans...
            if (isServiceBean(bean)) {
                Class<?> serviceType = bean.getBeanClass();
                Service serviceAnnotation = serviceType.getAnnotation(Service.class);

                appDescriptorSet.addDescriptor(new CDIBeanServiceDescriptor(bean, beanManager));
                if (serviceType.isInterface()) {
                    addInjectableClientProxyBean(bean, serviceType, serviceAnnotation, beanManager, abd);
                }
            }

            // Register all transformers we can find...
            if (Transformer.class.isAssignableFrom(bean.getBeanClass())) {
                Class<?> transformerRT = bean.getBeanClass();

                // TODO: Should probably only auto register a transformer based on an annotation or interface ??
                try {
                    ServiceDomains.getDomain().getTransformerRegistry().addTransformer((Transformer) transformerRT.newInstance());
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Invalid Transformer implementation '" + transformerRT.getName() + "'.", e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Invalid Transformer implementation '" + transformerRT.getName() + "'.", e);
                }
            }
        }
    }

    private void addInjectableClientProxyBean(Bean<?> serviceBean, Class<?> serviceType, Service serviceAnnotation, BeanManager beanManager, AfterBeanDiscovery abd) {
        QName serviceQName = toServiceQName(serviceType);
        addClientProxyBean(serviceQName, serviceType, null, abd);
    }

    private void addInjectableClientProxyBean(Field injectionPointField, Reference serviceReference, Set<Annotation> qualifiers, BeanManager beanManager, AfterBeanDiscovery abd) {
        QName serviceQName = toServiceQName(injectionPointField.getType());

        addClientProxyBean(serviceQName, injectionPointField.getType(), qualifiers, abd);
    }

    private void addClientProxyBean(QName serviceQName, Class<?> beanClass, Set<Annotation> qualifiers, AfterBeanDiscovery abd) {
        // Check do we already have a proxy for this service interface...
        for (ClientProxyBean clientProxyBean : _createdProxyBeans) {
            if (serviceQName.equals(clientProxyBean.getServiceQName()) && beanClass == clientProxyBean.getBeanClass()) {
                // ignore... we already have a proxy ...
                return;
            }
        }

        ClientProxyBean clientProxyBean = new ClientProxyBean(serviceQName, beanClass, qualifiers);
        _createdProxyBeans.add(clientProxyBean);
        abd.addBean(clientProxyBean);
    }

    private boolean isServiceBean(Bean<?> bean) {
        return bean.getBeanClass().isAnnotationPresent(Service.class);
    }

    private QName toServiceQName(Class<?> serviceType) {
        // TODO: Could use the bean class package name as the namespace component of the Service QName
        return new QName(serviceType.getSimpleName());
    }
}
