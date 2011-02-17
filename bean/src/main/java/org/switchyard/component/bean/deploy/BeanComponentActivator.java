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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.Service;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.deploy.Activator;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanComponentActivator implements Activator {

    private ApplicationServiceDescriptorSet _appDescriptorSet;
    private Map<QName, ComponentReferenceModel> _references = new HashMap<QName, ComponentReferenceModel>();

    public BeanComponentActivator() {
        _appDescriptorSet = ApplicationServiceDescriptorSet.lookup();
    }


    @Override
    public ExchangeHandler init(QName name, Model config) {
        if (config instanceof ComponentReferenceModel) {
            // policy and configuration validation can be performed here - 
            // nothing to do for now
            _references.put(name, (ComponentReferenceModel)config);
            return null;
        } else if (config instanceof ComponentServiceModel) {
            // lookup the handler for the initialized service
            for (ServiceDescriptor descriptor : _appDescriptorSet.getDescriptors()) {
                if(descriptor.getServiceName().equals(name)) {
                    return descriptor.getHandler();
                }
            }
        }
        // bean discovery did not find a bean providing this service
        throw new RuntimeException("Unknown Service name '" + name + "'.");
    }


    @Override
    public void start(Service service) {
        if (_references.containsKey(service.getName())) {
            // client proxies can be built here
        }
    }


    @Override
    public void stop(Service service) {
        // not sure this is significant for bean component
    }

    @Override
    public void destroy(Service service) {
        _references.remove(service.getName());
    }


}
