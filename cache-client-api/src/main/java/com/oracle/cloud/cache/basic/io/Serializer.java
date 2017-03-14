/*
 * File: Serializer.java
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

package com.oracle.cloud.cache.basic.io;

import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.SessionOption;

import com.oracle.cloud.cache.util.Options;

import java.io.IOException;

/**
 * Interface defining a serializer to Serializer class to serialize
 * to or from a byte[] to objects.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public interface Serializer extends SessionOption, CacheOption
{
    /**
     * Serializes an object into a byte array.
     *
     * @param o the object to serialize
     * @return a byte array
     * @throws IOException if an error occurs
     */
    byte[] serialize(Object o) throws IOException;


    /**
     * Deserializes a byte array into an object of a specified class.
     *
     * @param <T>     the type of the returned object
     * @param data    a byte array to deserialize
     * @param clzType the class of the created object
     * @return a deserialized object
     * @throws IOException if an error occurs
     */
    <T> T deserialize(byte[]   data,
                      Class<T> clzType) throws IOException;


    /**
     * Creates a custom serializer of a specified class.
     *
     * @param clz the class of the serializer to create
     * @return a Serializer instance
     */
    static Serializer of(Class<? extends Serializer> clz)
    {
        try
        {
            return clz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates a JSON serializer.
     *
     * @return a JSON serializer
     */
    @Options.Default
    static Serializer json()
    {
        return new JsonSerializer();
    }
}
