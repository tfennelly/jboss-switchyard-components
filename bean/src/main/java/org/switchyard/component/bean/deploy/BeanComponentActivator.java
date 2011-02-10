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

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.config.model.composite.InternalServiceModel;
import org.switchyard.deploy.ImplementationActivator;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentActivator implements ImplementationActivator {

    private ApplicationServiceDescriptorSet _appDescriptorSet;

    public BeanComponentActivator() {
        _appDescriptorSet = ApplicationServiceDescriptorSet.lookup();
    }

    @Override
    public void activateReference(QName name, Service service) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ExchangeHandler activateService(QName name,
            InternalServiceModel config) {
        for (ServiceDescriptor descriptor : _appDescriptorSet.getDescriptors()) {
            if(descriptor.getServiceName().equals(name)) {
                return descriptor.getHandler();
            }
        }

        throw new RuntimeException("Unknown Service name");
    }

    @Override
    public void deactivateReference(QName name) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deactivateService(Service service) {
        // TODO Auto-generated method stub
        
    }
}
