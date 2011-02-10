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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ApplicationServiceDescriptorSet {

    private static final String JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET = "java:comp/switchyard/ApplicationServiceDescriptorSet";

    private List<ServiceDescriptor> _descriptorSet = new ArrayList<ServiceDescriptor>();

    public void addDescriptor(ServiceDescriptor handler) {
        _descriptorSet.add(handler);
    }

    public List<ServiceDescriptor> getDescriptors() {
        return Collections.unmodifiableList(_descriptorSet);
    }
    
    public static ApplicationServiceDescriptorSet bind() {
        try {
            Context jndiContext = new InitialContext();

            try {
                ApplicationServiceDescriptorSet descriptorSet = new ApplicationServiceDescriptorSet();
                jndiContext.bind(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET, descriptorSet);
                return descriptorSet;
            } finally {
                jndiContext.close();
            }
        } catch (NamingException e) {
            throw new IllegalStateException("Unexpected NamingException getting JNDI Context.", e);
        }
    }

    public static ApplicationServiceDescriptorSet lookup() {
        try {
            Context jndiContext = new InitialContext();

            try {
                ApplicationServiceDescriptorSet descriptorSet = (ApplicationServiceDescriptorSet) 
                    jndiContext.lookup(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET);
                return descriptorSet;
            } finally {
                jndiContext.close();
            }
        } catch (NamingException e) {
            throw new IllegalStateException("Unexpected NamingException getting JNDI Context.", e);
        }
    }
}
