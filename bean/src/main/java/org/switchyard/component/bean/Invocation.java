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

import org.switchyard.Exchange;
import org.switchyard.internal.handlers.TransformSequence;

import java.lang.reflect.Method;

/**
 * Bean component invocation details.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Invocation {

    /**
     * The method/operation being invoked.
     */
    private Method method;
    /**
     * The exchange instance.
     */
    private Exchange exchange;

    /**
     * The invocation arguments.
     */
    private Object[] args;

    /**
     * Constructor.
     *
     * @param method The method/operation being invoked.
     * @param exchange The exchange instance.
     * @throws BeanComponentException Unsupported method structure, or type mismatch.
     */
    Invocation(Method method, Exchange exchange) throws BeanComponentException {
        this.method = method;
        this.exchange = exchange;
        this.args = castArg(method, exchange.getMessage().getContent());
        assertOK();
    }

    /**
     * Assert that the exchange payload type(s) and the bean method
     * argument type(s) match.
     */
    private void assertOK() throws BeanComponentException {
        if(!TransformSequence.assertTransformsApplied(exchange)) {
            String actualPayloadType = TransformSequence.getCurrentMessageType(exchange);
            String expectedPayloadType = TransformSequence.getTargetMessageType(exchange);

            throw new BeanComponentException("Bean service operation '" + operationName() + "' requires a payload type of '" + expectedPayloadType + "'.  Actual payload type is '" + actualPayloadType + "'.  You must define and register a Transformer to transform between these types.");
        }
        assertMethodStructureSupported();
        assertTypesMatch();
    }

    /**
     * Get the invocation arguments.
     *
     * @return The invocation arguments.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Get the method/operation being invoked.
     *
     * @return The method/operation being invoked.
     */
    public Method getMethod() {
        return method;
    }

    private static Object[] castArg(Method method, Object content) {
        if(method.getParameterTypes().length == 1 && content != null) {
            if (content.getClass().isArray()) {
                return (Object[].class).cast(content);
            } else {
                return new Object[]{content};
            }
        }
        return null;
    }

    private void assertMethodStructureSupported() throws BeanComponentException {
        Class<?>[] parameterTypes = method.getParameterTypes();

        // TODO: Only supports 0 or 1 arg operations for now...
        if(parameterTypes.length > 1) {
            throw new BeanComponentException("Bean service operation '" + operationName() + "' has more than 1 argument.  Bean component currently only supports single argument operations.");
        }
    }

    private void assertTypesMatch() throws BeanComponentException {
        if(args == null) {
            if(method.getParameterTypes().length != 0) {
                throw new BeanComponentException("Bean service operation '" + operationName() + "' requires a single argument.  Exchange payload specifies no payload.");
            }
        } else {
            if(args.length > 1) {
                throw new BeanComponentException("Bean service operation '" + operationName() + "' only supports a single argument.  Exchange payload specifies " + args.length + " args.");
            }

            if(args[0] != null) {
                Class<?> argType = method.getParameterTypes()[0];

                if(!argType.isInstance(args[0])) {
                    throw new BeanComponentException("Bean service operation '" + operationName() + "' requires a payload type of '" + argType.getName() + "'.  Actual payload type is '" + args[0].getClass().getName() + "'.  You must define and register a Transformer.");
                }
            }
        }
    }

    private String operationName() {
        return exchange.getService().getName() + "#" + method.getName();
    }
}
