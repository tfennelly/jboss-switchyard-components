/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package org.switchyard.component.soap.util;

import org.junit.Test;
import org.milyn.payload.StringSource;
import org.milyn.xml.XmlUtil;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SOAPUtilTest {

    String faultString = "<SOAP-ENV:Fault xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    +    "<faultcode>SOAP-ENV:Server</faultcode>"
                    +    "<faultstring>Send failed</faultstring>"
                    +    "<detail><FaultContents>"
                    +   "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<SOAP-ENV:Body xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">SOAP Fault yay!!</SOAP-ENV:Body>"
                    +    "</FaultContents></detail>"
                    + "</SOAP-ENV:Fault>";

    @Test
    public void testGenerateFault() throws Exception {
        SOAPMessage soapFault = SOAPUtil.SOAP_MESSAGE_FACTORY.createMessage();
        Document dom = XmlUtil.parseStream(new StringReader(faultString));

        soapFault.getSOAPPart().setContent(new DOMSource(dom));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapFault.writeTo(out);
        System.out.println(out.toString());


        System.out.println();
    }
}
