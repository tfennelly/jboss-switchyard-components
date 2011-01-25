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
import org.switchyard.component.soap.greeting.Reply;
import org.switchyard.transform.Transformer;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class JavaReplyToSOAP implements Transformer {

    private XMLBinding binder;

    public JavaReplyToSOAP() throws IOException, SAXException {
        binder = new XMLBinding().add("/transforms/ReplySOAPBinding.xml").intiailize();
    }

    @Override
    public Object transform(Object from) {
        return binder.toXML(from);
    }

    @Override
    public String getFrom() {
        // TODO: How about just "java:/org.switchyard.component.soap.greeting.Reply" ?
        // What we currently have coming from the JavaService class looks more like a sig for the operation itself Vs just the operation payload, no?
        return "java:/org.switchyard.component.soap.greeting.GreetingService/greet/org.switchyard.component.soap.greeting.Reply";
    }

    @Override
    public String getTo() {
        return "{http://greeting.soap.component.switchyard.org/}greetResponse";
    }

    @Override
    public Class getFromType() {
        return Reply.class;
    }

    @Override
    public Class getToType() {
        return String.class;
    }
}
