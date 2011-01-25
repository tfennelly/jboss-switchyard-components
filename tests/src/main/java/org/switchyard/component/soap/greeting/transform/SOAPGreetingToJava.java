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
package org.switchyard.component.soap.greeting.transform;

import org.milyn.javabean.binding.xml.XMLBinding;
import org.switchyard.component.soap.greeting.Greeting;
import org.switchyard.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.transform.dom.DOMSource;
import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SOAPGreetingToJava implements Transformer {

    private XMLBinding binder;

    public SOAPGreetingToJava() throws IOException, SAXException {
        binder = new XMLBinding().add("/transforms/GreetingSOAPBinding.xml").intiailize();
    }

    @Override
    public Object transform(Object from) {
        try {
            return binder.fromXML(new DOMSource((Node) from), Greeting.class);
        } catch (IOException e) {
            // TODO: Need an exception type here !!
            throw new IllegalStateException("Transformation from '" + getFrom() + "' to '" + getTo() + "' failed.", e);
        }
    }

    @Override
    public String getFrom() {
        return "{http://greeting.soap.component.switchyard.org/}greet"; // SOAP body element QName
    }

    @Override
    public String getTo() {
        // TODO: How about just "java:/org.switchyard.component.soap.greeting.Greeting" ?
        // What we currently have coming from the JavaService class looks more like a sig for the operation itself Vs just the operation payload, no?
        return "java:/org.switchyard.component.soap.greeting.GreetingService/greet/org.switchyard.component.soap.greeting.Greeting";
    }

    @Override
    public Class getFromType() {
        return Node.class;
    }

    @Override
    public Class getToType() {
        return Greeting.class;
    }
}
