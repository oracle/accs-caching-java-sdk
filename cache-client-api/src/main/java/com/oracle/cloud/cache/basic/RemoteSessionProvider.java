/*
 * File: RemoteSessionProvider.java
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
import java.net.URISyntaxException;

import com.oracle.cloud.cache.basic.options.SessionOption;
import com.oracle.cloud.cache.basic.options.Transport;
import com.oracle.cloud.cache.util.Options;

/**
 * An implementation of a {@link SessionProvider} which connects to an
 * Application Container Cloud Service (ACCS) Application Cache
 * using either <a href="https://github.com/grpc/grpc-java">GRPC</a> or REST transport.
 *
 * @author Aleksandar Seovic  2016.06.02
 */
public class RemoteSessionProvider implements SessionProvider
{
    /**
     * URI for the remote session.
     */
    private URI uri;


    /**
     * No-args constructor to use the defined system property "cloud.cache.uri".
     */
    public RemoteSessionProvider()
    {
        this(System.getProperty("cloud.cache.uri", "http://localhost:1444/"));
    }


    /**
     * Constructs a RemoteSessionProvider given a {@link String}.
     *
     * @param uri the uri String
     */
    public RemoteSessionProvider(String uri)
    {
        try
        {
            this.uri = new URI(uri);
        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("invalid URI", e);
        }
    }


    /**
     * Constructs a RemoteSessionProvider given a {@link URI}.
     *
     * @param uri the {@link URI}
     */
    public RemoteSessionProvider(URI uri)
    {
        this.uri = uri;
    }


    /**
     * Creates a new Session to a Application Container Cloud Service (ACCS) Application Cache
     * with the specified {@link SessionOption}.
     *
     * @param options the cache session options
     * @return the Session
     */
    @Override
    public Session createSession(SessionOption... options)
    {
        Transport transport = Options.from(SessionOption.class, options).get(Transport.class);

        return transport.getType() == Transport.Type.GRPC ? new GrpcSession(uri,
                                                                            options) : new RestSession(uri, options);
    }


    /**
     * Returns the url for this RemoteSessionProvider.
     *
     * @return the url for this RemoteSessionProvider
     */
    public URI getUri()
    {
        return uri;
    }
}
