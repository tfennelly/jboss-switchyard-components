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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.xml.namespace.QName;
import java.io.InputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@ApplicationScoped
public class SwitchYardCDIBootstrapper implements Extension {

    private ApplicationServiceDescriptorSet appDescriptorSet;

    /**
     * {@link AfterDeploymentValidation} CDI event observer.
     *
     * @param event         CDI Event instance.
     */
    public void afterDeploymentValidation(@Observes AfterDeploymentValidation event) {
        InputStream swConfigStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/switchyard.xml");

        if(swConfigStream != null) {
            // Fire deploy, passing the switchyard config...

//            Keith's BeanComponent can do the following to get the services from java:comp...
//
//            ApplicationServiceDescriptorSet appDescriptorSet = ApplicationServiceDescriptorSet.lookup();
//
//            for (ServiceDescriptor descriptor : appDescriptorSet.getDescriptors()) {
//                QName serviceName = descriptor.getServiceName();
//                ExchangeHandler serviceHandler = descriptor.getHandler();
//
//                // etc....
//            }
        }
    }

    /**
     * {@link BeforeShutdown} CDI event observer.
     *
     * @param event       CDI Event instance.
     */
    public void beforeShutdown(@Observes BeforeShutdown event) {
        if(appDescriptorSet != null) {
            // Fire the undeploy...
        }
    }
}
