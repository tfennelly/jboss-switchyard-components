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

package org.switchyard.component.camel.deploy;

import org.apache.camel.spi.Registry;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.switchyard.component.bean.deploy.BeanDeploymentMetaData;
import org.switchyard.component.bean.deploy.CDIBean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIAwareRegistry implements Registry {

    /**
     * Logger
     */
    private static Logger _logger = Logger.getLogger(CDIAwareRegistry.class);

    private BeanDeploymentMetaData _beanDeploymentMetaData;
    private Registry _baseRegistry;

    public CDIAwareRegistry(BeanDeploymentMetaData serviceDescriptors, Registry baseRegistry) {
        this._beanDeploymentMetaData = serviceDescriptors;
        this._baseRegistry = baseRegistry;
    }


	public Object lookup(final String name) {
		Validate.notEmpty(name, "name");
        if (_logger.isDebugEnabled()) {
		    _logger.debug("Looking up bean using name = [" + name + "] in CDI registry ...");
        }

        CDIBean cdiBean = getBean(name);
		if (cdiBean == null) {
			return _baseRegistry.lookup(name);
		}
        if (_logger.isDebugEnabled()) {
            _logger.debug("Found SwitchYard Service bean matching name = [" + name + "] in CDI registry.");
        }

        return createBeanInstance(cdiBean);
	}

    /**
	 * @see org.apache.camel.spi.Registry#lookup(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public <T> T lookup(final String name, final Class<T> type) {
		Validate.notEmpty(name, "name");
		Validate.notNull(type, "type");
        if (_logger.isDebugEnabled()) {
		    _logger.debug("Looking up bean using name = [" + name + "] having expected type = [" + type.getName() + "] in CDI registry ...");
        }

		return type.cast(lookup(name));
	}

	/**
	 * @see org.apache.camel.spi.Registry#lookupByType(java.lang.Class)
	 */
	@Override
	public <T> Map<String, T> lookupByType(final Class<T> type) {
		Validate.notNull(type, "type");
        if (_logger.isDebugEnabled()) {
		    _logger.debug("Looking up all beans having expected type = [" + type.getName() + "] in CDI registry ...");
        }

        List<CDIBean> serviceBeans = getBeans(type);
		if (serviceBeans.isEmpty()) {
			return _baseRegistry.lookupByType(type);
		}

        if (_logger.isDebugEnabled()) {
		    _logger.debug("Found [" + Integer.valueOf(serviceBeans.size()) + "] beans having expected type = [" + type.getName() + "] in CDI registry.");
        }

		Map<String, T> beansByName = new HashMap<String, T>(serviceBeans.size());
        for (CDIBean cdiBean : serviceBeans) {
			beansByName.put(toBeanName(cdiBean), type.cast(createBeanInstance(cdiBean)));
		}

		return beansByName;
	}

    private CDIBean getBean(String name) {
        for (CDIBean cdiBean : _beanDeploymentMetaData.getDeploymentBeans()) {
            if (toBeanName(cdiBean).equals(name)) {
                return cdiBean;
            }
        }

        return null;
    }

    private List<CDIBean> getBeans(Class<?> type) {
        List<CDIBean> cdiBeans = new ArrayList<CDIBean>();

        for (CDIBean cdiBean : _beanDeploymentMetaData.getDeploymentBeans()) {
            if (type.isAssignableFrom(cdiBean.getBean().getBeanClass())) {
                cdiBeans.add(cdiBean);
            }
        }

        return cdiBeans;
    }

    private Object createBeanInstance(CDIBean cdiBean) {
        BeanManager beanManager = cdiBean.getBeanManager();
        Bean bean = cdiBean.getBean();
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(null);

        return beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
    }

    private String toBeanName(CDIBean cdiBean) {
        Bean bean = cdiBean.getBean();
        String beanName = bean.getName();

        if (beanName == null) {
            Class beanClass = bean.getBeanClass();
            beanName = beanClass.getSimpleName();
        }

        return beanName;
    }
}
