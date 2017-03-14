/*
 * File: RestSession.java
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

package com.oracle.cloud.cache.basic;

import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.SessionOption;
import com.oracle.cloud.cache.rest.JacksonMapperProvider;
import com.oracle.cloud.cache.rest.MultiValue;
import com.oracle.cloud.cache.util.Options;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * An implementation of a {@link Session} which uses REST as transport
 * to connect to an Application Container Cloud Service (ACCS) Application Cache.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class RestSession implements Session
{
    /**
     * {@link Options} for this session.
     */
    private final Options<SessionOption> options;

    /**
     * Base target for this session.
     */
    private WebTarget target;


    /**
     * Create a mew RestSession with the given uri and options.
     *
     * @param uri     uri to be used
     * @param options options to be applied
     */
    public RestSession(URI              uri,
                       SessionOption... options)
    {
        Client client =
            ClientBuilder.newBuilder().register(MultiValue.Reader.class).register(MultiValue.Writer.class)
            .register(JacksonMapperProvider.class).register(JacksonFeature.class).build();

        this.options = Options.from(SessionOption.class, options);
        this.target  = client.target(uri);
    }


    @Override
    public <V> Cache<V> getCache(String         cacheName,
                                 CacheOption... options)
    {
        return new RestCache<>(cacheName, this, options);
    }


    /**
     * Returns the {@link SessionOption}s for this session.
     *
     * @return the {@link SessionOption}s for this session
     */
    public Options<SessionOption> getOptions()
    {
        return options;
    }


    /**
     * Returns the {@link WebTarget} for this session.
     *
     * @return the {@link WebTarget} for this session
     */
    public WebTarget getTarget()
    {
        return target;
    }


    /**
     * Sets the {@link WebTarget} for this session.
     *
     * @param target {@link WebTarget} for this session
     */
    public void setTarget(WebTarget target)
    {
        this.target = target;
    }


    @Override
    public String toString()
    {
        return "RestSession{" + "options=" + Arrays.toString(options.asArray()) + ", target=" + target + '}';
    }
}