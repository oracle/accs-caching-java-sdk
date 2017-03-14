/*
 * File: MultiValue.java
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not use this file except in compliance with the Universal Permissive
 * License (UPL), Version 1.0 (the "License.")
 *
 * You may obtain a copy of the License at https://opensource.org/licenses/UPL.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */

package com.oracle.cloud.cache.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Represents multiple values. Each value is encoded as a byte array,
 * which the MultiValue object treats as an opaque blob.
 * The structure is as follows:<br>
 * <ul>
 *     <li>N (an int), the number of values encoded, followed by</li>
 *     <li>L (an int), the length of the first encoded value, followed by</li>
 *     <li>A byte array, which is the encoded value</li>
 * </ul>
 * <br>
 * The last two items above are repeated N times.
 * @author Aleksandar Seovic  2016.06.14
 */
public class MultiValue
{
    /**
     * {@link List} of byte[] values.
     */
    private final List<byte[]> values;

    /**
     * Number of MultiValue.
     */
    private final int size;


    /**
     * Constructs a new MultiValue from one ore more byte[].
     *
     * @param values  array of byte[] values to be encoded as MultiValue.
     */
    public MultiValue(byte[]... values)
    {
        this(Arrays.asList(values));
    }


    /**
     * Constructs a new MultiValue from a {@link List} of byte[].
     *
     * @param values {@link List} of byte[] to read from
     */
    private MultiValue(List<byte[]> values)
    {
        this.values = values;
        size        = values.stream().mapToInt(abValue -> abValue.length).sum() + 4 + values.size() * 4;
    }


    /**
     * Returns a byte array of the MultiValue contents.
     *
     * @return a byte array of the MultiValue contents
     *
     * @throws IOException if any I/O related errors
     */
    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);

        writeTo(out);

        return out.toByteArray();
    }


    /**
     * Writes the MultiValue to the given {@link OutputStream} in encoded format.
     *
     * @param output   {@link OutputStream} to write to
     *
     * @throws IOException if any I/O related errors
     */
    private void writeTo(OutputStream output) throws IOException
    {
        DataOutputStream out = new DataOutputStream(output);

        out.writeInt(values.size());

        for (byte[] abValue : values)
        {
            out.writeInt(abValue.length);
            out.write(abValue);
        }
    }


    /**
     * Creates a new MultiValue from an encoded array of bytes. For a description of the encoding,
     * see the MultiValue introductory section.
     *
     * @param abValues  bytes to read from
     *
     * @return MultiValue instance
     *
     * @throws IOException  if any I/O related errors
     */
    public static MultiValue from(byte[] abValues) throws IOException
    {
        return from(new ByteArrayInputStream(abValues));
    }


    /**
     * Creates a new MultiValue from an encoded {@link InputStream}.
     * For a description of the encoding, see the MultiValue introductory section.
     *
     * @param input  {@link InputStream} to read from
     *
     * @return MultiValue instance
     *
     * @throws IOException  if any I/O related errors
     */
    public static MultiValue from(InputStream input) throws IOException
    {
        DataInputStream in     = new DataInputStream(input);

        int             count = in.readInt();

        // protect against potential large values of count due to
        // spam data being sent. Large value could cause OOME on new ArrayList().
        if (count > 1000)
        {
            throw new IOException("Invalid value of " + count + " for MultiValue");
        }

        List<byte[]> values = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            int    nLength = in.readInt();
            byte[] abValue = new byte[nLength];

            in.readFully(abValue);
            values.add(abValue);
        }

        return new MultiValue(values);
    }


    /**
     * Returns the value at the given index.
     *
     * @param index  index to return value at
     *
     * @return the value at the given index
     */
    public byte[] get(int index)
    {
        return values.get(index);
    }


    /**
     * Class to read {@link MultiValue}.
     */
    public static class Reader implements MessageBodyReader<MultiValue>
    {
        @Override
        public boolean isReadable(Class<?>     type,
                                  Type         genericType,
                                  Annotation[] annotations,
                                  MediaType    mediaType)
        {
            return MultiValue.class.equals(type);
        }


        @Override
        public MultiValue readFrom(Class<MultiValue>              type,
                                   Type                           genericType,
                                   Annotation[]                   annotations,
                                   MediaType                      mediaType,
                                   MultivaluedMap<String, String> httpHeaders,
                                   InputStream                    entityStream) throws IOException, WebApplicationException
        {
            return MultiValue.from(entityStream);
        }
    }


    /**
     * Class to write {@link MultiValue}.
     */
    public static class Writer implements MessageBodyWriter<MultiValue>
    {
        @Override
        public boolean isWriteable(Class<?>     type,
                                   Type         genericType,
                                   Annotation[] annotations,
                                   MediaType    mediaType)
        {
            return MultiValue.class.equals(type);
        }


        @Override
        public long getSize(MultiValue   multiValue,
                            Class<?>     type,
                            Type         genericType,
                            Annotation[] annotations,
                            MediaType    mediaType)
        {
            return multiValue.size;
        }


        @Override
        public void writeTo(MultiValue                     multiValue,
                            Class<?>                       type,
                            Type                           genericType,
                            Annotation[]                   annotations,
                            MediaType                      mediaType,
                            MultivaluedMap<String, Object> httpHeaders,
                            OutputStream                   entityStream) throws IOException, WebApplicationException
        {
            multiValue.writeTo(entityStream);
        }
    }
}
