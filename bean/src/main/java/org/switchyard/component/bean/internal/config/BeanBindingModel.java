/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.switchyard.component.bean.internal.config;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

import javax.xml.namespace.QName;

/**
 * A model that holds the Bean component configuration.
 * 
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanBindingModel extends V1BindingModel {

    /**
     *  Prefix for SOAP Gateway Configuration.
     */
    public static final String BEAN = "bean";

    /**
     * Constructor.
     */
    public BeanBindingModel() {
        super(BEAN);
    }

    /**
     * Create a SOAPBindingModel using configuration and descriptor.
     *
     * @param config the SOAPGateway configuration
     * @param desc the SOAPGateway descriptor
     */
    public BeanBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }
}
