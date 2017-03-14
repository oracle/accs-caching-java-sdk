/*
 * File: GrpcCache.java
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

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.oracle.cloud.cache.ServerCacheMetrics;
import com.oracle.cloud.cache.basic.io.Serializer;
import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.Expiry;

/**
 * An implementation of the {@link Cache} interface which uses
 * <a href="https://github.com/grpc/grpc-java">GRPC</a> to issue requests
 * to Application Container Cloud Service (ACCS) Application Cache.
 *
 * @param <V> Value type for cache
 * @author Aleksandar Seovic/Tim Middleton  2016.05.20
 */
public class GrpcCache<V> extends AbstractCache<V>
{
    /**
     * The {@link GrpcSession} to use.
     */
    private final GrpcSession grpcSession;

    /**
     * The client which will talk GRPC for cache operations.
     */
    private final CacheGrpc.CacheBlockingStub client;

    /**
     * The serializer for the cache.
     */
    private final Serializer serializer;


    /**
     * Constructs a GrpcCache given a cache, session and options.
     *
     * @param cacheName   the cache name
     * @param grpcSession the grpc session
     * @param options     options to apply
     */
    @SuppressWarnings("unchecked")
    GrpcCache(String         cacheName,
              GrpcSession    grpcSession,
              CacheOption... options)
    {
        super(cacheName, options);

        this.grpcSession = grpcSession;
        this.serializer  = getOptions().get(Serializer.class, grpcSession.getOptions().get(Serializer.class));
        this.client      = CacheGrpc.newBlockingStub(grpcSession.getChannel());
    }


    @Override
    protected V get(String key)
    {
        try
        {
            CacheRpc.GetResponse response   = client.get(CacheProtocol.getRequest(getCacheName(), key));
            ByteString           byteString = response.getValue();

            return serializer.deserialize(byteString.toByteArray(), getValueClass());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V put(String  key,
                    V       value,
                    Expiry  expiry,
                    boolean returnOld)
    {
        try
        {
            CacheRpc.PutResponse response = client.put(CacheProtocol.putRequest(getCacheName(),
                                                                                key,
                                                                                serializer.serialize(value),
                                                                                expiry.getExpiry(),
                                                                                returnOld));

            return returnOld ? serializer.deserialize(response.getValue().toByteArray(), getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V putIfAbsent(String  key,
                            V       value,
                            Expiry  expiry,
                            boolean returnOld)
    {
        try
        {
            CacheRpc.PutResponse response = client.putIfAbsent(CacheProtocol.putRequest(getCacheName(),
                                                                                        key,
                                                                                        serializer.serialize(value),
                                                                                        expiry.getExpiry(),
                                                                                        returnOld));

            ByteString byteString = response.getValue();

            return returnOld
                   && byteString != null
                   && byteString.size() > 0 ? serializer.deserialize(byteString.toByteArray(), getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V replace(String  key,
                        V       value,
                        Expiry  expiry,
                        boolean returnOld)
    {
        try
        {
            CacheRpc.ReplaceResponse response = client.replace(CacheProtocol.replaceRequest(getCacheName(),
                                                                                            key,
                                                                                            serializer.serialize(value),
                                                                                            expiry.getExpiry(),
                                                                                            returnOld));

            return returnOld ? serializer.deserialize(response.getValue().toByteArray(), getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected boolean replaceValue(String key,
                                   V      valueOld,
                                   V      valueNew,
                                   Expiry expiry)
    {
        try
        {
            CacheRpc.ReplaceValueResponse response =
                client.replaceValue(CacheProtocol.replaceValueRequest(getCacheName(),
                                                                      key,
                                                                      serializer.serialize(valueOld),
                                                                      serializer.serialize(valueNew),
                                                                      expiry.getExpiry()));

            return response.getSuccess();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V remove(String  key,
                       boolean returnOld)
    {
        try
        {
            CacheRpc.RemoveResponse response = client.remove(CacheProtocol.removeRequest(getCacheName(),
                                                                                         key,
                                                                                         returnOld));

            return returnOld ? serializer.deserialize(response.getValue().toByteArray(), getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected boolean removeValue(String key,
                                  V      value)
    {
        try
        {
            CacheRpc.RemoveValueResponse response = client.removeValue(CacheProtocol.removeValueRequest(getCacheName(),
                                                                                                        key,
                                                                                                        serializer.serialize(value)));

            return response.getSuccess();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected ServerCacheMetrics getServerMetrics()
    {
        CacheRpc.MetricsResponse response = client.getMetrics(CacheProtocol.metricsRequest(getCacheName()));

        return new ServerCacheMetrics(response.getCount(), response.getSize());
    }


    @Override
    public void clear()
    {
        client.clear(CacheProtocol.clearRequest(getCacheName()));
    }
}
