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

package org.switchyard.component.bean.deploy;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentActivator implements ComponentActivator {

    private ServiceDomain _serviceDomain;
    private ApplicationServiceDescriptorSet _appDescriptorSet;

    public BeanComponentActivator(ServiceDomain serviceDomain) {
        this._serviceDomain = serviceDomain;
        _appDescriptorSet = ApplicationServiceDescriptorSet.lookup();
    }

    public ExchangeHandler activate(QName serviceName) {
        for (ServiceDescriptor descriptor : _appDescriptorSet.getDescriptors()) {
            if(descriptor.getServiceName().equals(serviceName)) {
                return descriptor.getHandler();
            }
        }

        throw new RuntimeException("Unknown Service name");
    }
}
