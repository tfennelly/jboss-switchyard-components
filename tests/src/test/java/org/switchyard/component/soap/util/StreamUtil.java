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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StreamUtil {

    /**
     * Read the supplied InputStream and return as a byte array.
     *
     * @param stream The stream to read.
     * @return byte array containing the Stream data.
     * @throws java.io.IOException Exception reading from the stream.
     */
    public static byte[] readBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] byteBuf = new byte[1024];
        int readCount = 0;

        while ((readCount = stream.read(byteBuf)) != -1) {
            bytesOut.write(byteBuf, 0, readCount);
        }

        return bytesOut.toByteArray();
    }

    /**
     * Read the supplied InputStream and return as a String.
     *
     * @param stream The stream to read.
     * @return A String containing the Stream data.
     * @throws IOException Exception reading from the stream.
     */
    public static String readString(InputStream stream) throws IOException {
        // Encoding not considered... fine for testing...
        return new String(readBytes(stream));
    }

}
