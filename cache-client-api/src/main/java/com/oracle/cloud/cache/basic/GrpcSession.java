/*
 * File: GrpcSession.java
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

import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.SessionOption;
import com.oracle.cloud.cache.util.Options;
import io.grpc.Channel;
import io.grpc.netty.NettyChannelBuilder;

/**
 * An implementation of a {@link Session} which uses <a href="https://github.com/grpc/grpc-java">GRPC</a>
 * as transport to connect to an Application Container Cloud Service (ACCS) Application Cache
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class GrpcSession implements Session
{
    /**
     * The URI for this session.
     */
    private final URI uri;

    /**
     * {@link Options} for this session.
     */
    private final Options<SessionOption> options;

    /**
     * {@link Channel} for this session.
     */
    private Channel channel;


    /**
     * Creates a new GrpcSession with the given URI and {@link SessionOption}s.
     *
     * @param uri     URI to connect to
     * @param options array of {@link SessionOption}s
     */
    public GrpcSession(URI              uri,
                       SessionOption... options)
    {
        this.uri     = uri;
        this.options = Options.from(SessionOption.class, options);
        this.channel = createChannel();
    }


    /**
     * Creates a {@link Channel} using the URI.
     *
     * @return a new {@link Channel}
     */
    private Channel createChannel()
    {
        if (this.uri == null)
        {
            throw new IllegalArgumentException("URI must be specified");
        }

        return NettyChannelBuilder.forAddress(uri.getHost(), uri.getPort())
                .maxInboundMessageSize(getMaxMessageSize())
                .usePlaintext(true)
                .build();
    }


    @Override
    public <V> Cache<V> getCache(String         cacheName,
                                 CacheOption... options)
    {
        return new GrpcCache<>(cacheName, this, options);
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
     * Returns the {@link Channel} for this session.
     *
     * @return the {@link Channel} for this session
     */
    public Channel getChannel()
    {
        return channel;
    }


    /**
     * Sets the {@link Channel} for this session. <br>
     * <strong>Note:</strong> This method is for advanced use cases only. Incorrect use may cause the API not
     * to function properly.
     *
     * @param channel the {@link Channel} for this session
     */
    protected void setChannel(Channel channel)
    {
        this.channel = channel;
    }


    @Override
    public String toString()
    {
        return "GrpcSession{" + "uri=" + uri + ", options=" + Arrays.toString(options.asArray()) + '}';
    }


    /**
     * Returns the maximum message size in MB as defined by system property ccs.maxMessageSizeMB.
     * Value is checked to ensure it is no greater than 256 MB and is defaulted to 4MB.
     *
     * @return Returns the maximum message size in MB
     */
    static int getMaxMessageSize()
    {
        final String propertyName = "ccs.maxMessageSizeMB";

        int          max          = Integer.parseInt(System.getProperty(propertyName, "4"));

        if (max >= 256)
        {
            throw new IllegalArgumentException("Value for " + propertyName + " must be less tha 256");
        }

        return max * 1024 * 1024;
    }
}
