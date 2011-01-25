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

package org.switchyard.component.soap.util;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class WebServiceInvoker {

    public static String invoke(String serviceUrl, String soap) {
        try {
            return invoke(new URL(serviceUrl), soap);
        } catch (MalformedURLException e) {
            Assert.fail("Error creating a URL instance for service URL '" + serviceUrl + "'.");
            return null; // happy compiler
        }
    }

    public static String invoke(URL serviceUrl, String soap) {
        String output = null;

        try {
            HttpURLConnection con = (HttpURLConnection) serviceUrl.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
            OutputStream outStream = con.getOutputStream();
            System.out.println("-----------SOAP Request-----------");
            System.out.println(soap);
            System.out.println("----------------------------------");
            outStream.write(soap.getBytes());
            InputStream inStream = con.getInputStream();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byte[] byteBuf = new byte[256];
            int len = inStream.read(byteBuf);
            while (len > -1) {
                byteStream.write(byteBuf, 0, len);
                len = inStream.read(byteBuf);
            }
            outStream.close();
            inStream.close();
            byteStream.close();
            output =  byteStream.toString();

            System.out.println("-----------SOAP Response-----------");
            System.out.println(output);
            System.out.println("----------------------------------");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            output = "<error>" + ioe + "</error>";
        }

        return output;
    }


}
