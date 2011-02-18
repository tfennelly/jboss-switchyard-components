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

import org.switchyard.component.bean.ClientProxyBean;
import org.switchyard.transform.Transformer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanDeploymentMetaData implements Serializable {

    private static final String JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET = "cn=SwitchyardApplicationServiceDescriptorSet";

    private List<ServiceDescriptor> _serviceDescriptors = new ArrayList<ServiceDescriptor>();
    private List<ClientProxyBean> _clientProxies = new ArrayList<ClientProxyBean>();
    private List<Transformer> _transformers = new ArrayList<Transformer>();

    public void addServiceDescriptor(ServiceDescriptor handler) {
        _serviceDescriptors.add(handler);
    }

    public void addClientProxy(ClientProxyBean proxy) {
        _clientProxies.add(proxy);
    }

    public void addTransformer(Transformer transformer) {
        _transformers.add(transformer);
    }

    public List<ServiceDescriptor> getServiceDescriptors() {
        return Collections.unmodifiableList(_serviceDescriptors);
    }

    public List<ClientProxyBean> getClientProxies() {
        return Collections.unmodifiableList(_clientProxies);
    }

    public List<Transformer> getTransformers() {
        return Collections.unmodifiableList(_transformers);
    }

    public static BeanDeploymentMetaData bind() {
        Map<ClassLoader, BeanDeploymentMetaData> metaDataMap = getBeanDeploymentMetaDataMap();
        BeanDeploymentMetaData deploymentMetaData = new BeanDeploymentMetaData();

        metaDataMap.put(Thread.currentThread().getContextClassLoader(), deploymentMetaData);

        return deploymentMetaData;
    }

    public static BeanDeploymentMetaData lookup() {
        return getBeanDeploymentMetaDataMap().get(Thread.currentThread().getContextClassLoader());
    }

    public static void unbind() {
        getBeanDeploymentMetaDataMap().remove(Thread.currentThread().getContextClassLoader());
    }

    private synchronized static Map<ClassLoader, BeanDeploymentMetaData> getBeanDeploymentMetaDataMap() {
        try {
            Context jndiContext = new InitialContext();

            try {
                Map<ClassLoader, BeanDeploymentMetaData> descriptorMap = (Map<ClassLoader, BeanDeploymentMetaData>)
                            jndiContext.lookup(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET);

                return descriptorMap;
            } finally {
                jndiContext.close();
            }
        } catch (NamingException e1) {
            try {
                Context jndiContext = new InitialContext();

                try {
                    Map<ClassLoader, BeanDeploymentMetaData> descriptorMap =
                            new ConcurrentHashMap<ClassLoader, BeanDeploymentMetaData>();
                    jndiContext.bind(JAVA_COMP_SWITCHYARD_SERVICE_DESCRIPTOR_SET, descriptorMap);

                    return descriptorMap;
                } finally {
                    jndiContext.close();
                }
            } catch (NamingException e2) {
                throw new IllegalStateException("Unexpected NamingException getting JNDI Context.", e2);
            }
        }
    }
}
