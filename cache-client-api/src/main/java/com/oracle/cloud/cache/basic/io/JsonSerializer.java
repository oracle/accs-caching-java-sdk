/*
 * File: JsonSerializer.java
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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serializer class to serialize Objects into byte[] (as JSON) and the reverse
 * using {@link ObjectMapper}.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class JsonSerializer implements Serializer
{
    /**
     * The {@link ObjectMapper} to be used to parse JSON.
     */
    private final ObjectMapper mapper;


    /**
     * Creates a new serializer instance.
     */
    public JsonSerializer()
    {
        mapper = new ObjectMapper();
    }


    @Override
    public byte[] serialize(Object o) throws IOException
    {
        return mapper.writeValueAsBytes(o);
    }



    @Override
    public <T> T deserialize(byte[]   data,
                             Class<T> clzType) throws IOException
    {
        return data == null || data.length == 0 ? null : mapper.readValue(data, clzType);
    }
}
