/*
 * File: JacksonMapperProvider.java
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

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;   
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * Provider responsible for converting Java object to/from JSON/XML.
 *
 * @author Luk Ho 2014.07.18
 *
 */
@Provider
public class JacksonMapperProvider implements ContextResolver<ObjectMapper>
{
    /**
     * Default mapper.
     */
    static final ObjectMapper DEFAULT_OBJECT_MAPPING = createDefaultMapper();


    /**
     * No-args constructor.
     */
    public JacksonMapperProvider()
    {
    }


    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return DEFAULT_OBJECT_MAPPING;
    }


    /**
     * Returns the {@link ObjectMapper}.
     *
     * @return the {@link ObjectMapper}
     */
    public static ObjectMapper getObjectMapper()
    {
        return DEFAULT_OBJECT_MAPPING;
    }


    /**
     * Creates a default {@link ObjectMapper}.
     *
     * @return a new {@link ObjectMapper}
     */
    private static ObjectMapper createDefaultMapper()
    {
        final ObjectMapper   mapper = new ObjectMapper();

        JaxbAnnotationModule module = new JaxbAnnotationModule();

        mapper.registerModule(module);

        mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        mapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        return mapper;
    }
}
