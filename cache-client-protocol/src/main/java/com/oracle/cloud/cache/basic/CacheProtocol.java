/*
 * File: CacheProtocol.java
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

import com.google.protobuf.ByteString;
import com.oracle.cloud.cache.ServerCacheMetrics;

/**
 * Various factory methods for interacting with an Application Container Cloud Service (ACCS) Application Cache
 * via <a href="https://github.com/grpc/grpc-java">GRPC</a> protocol.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
class CacheProtocol
{
    /**
     * An empty response.
     */
    private static final CacheRpc.EmptyResponse EMPTY = CacheRpc.EmptyResponse.newBuilder().build();


    /**
     * Factory method for GetRequest.
     *
     * @param cacheName  cache name
     * @param key        cache key
     * @return GetRequest instance
     */
    static CacheRpc.GetRequest getRequest(String cacheName,
                                          String key)
    {
        return CacheRpc.GetRequest.newBuilder().setCache(cacheName).setKey(key).build();
    }


    /**
     * Factory method for GetResponse.
     *
     * @param value returned cache value
     *
     * @return GetResponse instance
     */
    static CacheRpc.GetResponse getResponse(byte[] value)
    {
        return CacheRpc.GetResponse.newBuilder().setValue(value == null
                                                          ? ByteString.EMPTY : ByteString.copyFrom(value)).build();
    }


    /**
     * Factory method for PutRequest.
     *
     * @param cacheName  cache name
     * @param key        cache key
     * @param value      cache value
     * @param ttl        entry's time-to-live
     * @param returnOld  the flag specifying whether to return the old value
     * @return PutRequest instance
     */
    static CacheRpc.PutRequest putRequest(String  cacheName,
                                          String  key,
                                          byte[]  value,
                                          long    ttl,
                                          boolean returnOld)
    {
        return CacheRpc.PutRequest.newBuilder().setCache(cacheName).setKey(key).setValue(ByteString.copyFrom(value))
        .setTtl(ttl).setReturn(returnOld).build();
    }


    /**
     * Factory method for PutResponse.
     *
     * @param value old value to return (if requested)
     * @return PutResponse instance
     */
    static CacheRpc.PutResponse putResponse(byte[] value)
    {
        CacheRpc.PutResponse.Builder builder = CacheRpc.PutResponse.newBuilder();

        if (value != null)
        {
            builder.setValue(ByteString.copyFrom(value));
        }

        return builder.build();
    }


    /**
     * Factory method for RemoveRequest.
     *
     * @param cacheName  cache name
     * @param key        cache key
     * @param returnOld  the flag specifying whether to return the old value
     * @return RemoveRequest instance
     */
    static CacheRpc.RemoveRequest removeRequest(String  cacheName,
                                                String  key,
                                                boolean returnOld)
    {
        return CacheRpc.RemoveRequest.newBuilder().setCache(cacheName).setKey(key).setReturn(returnOld).build();
    }


    /**
     * Factory method for RemoveResponse.
     *
     * @param value  old value to return (if requested)
     * @return RemoveResponse instance
     */
    static CacheRpc.RemoveResponse removeResponse(byte[] value)
    {
        CacheRpc.RemoveResponse.Builder builder = CacheRpc.RemoveResponse.newBuilder();

        if (value != null)
        {
            builder.setValue(ByteString.copyFrom(value));
        }

        return builder.build();
    }


    /**
     * Factory method for RemoveValueRequest.
     *
     * @param cacheName  cache name
     * @param key        cache key
     * @param oldValue   value to match to remove
     * @return RemoveValueRequest instance
     */
    static CacheRpc.RemoveValueRequest removeValueRequest(String cacheName,
                                                          String key,
                                                          byte[] oldValue)
    {
        return CacheRpc.RemoveValueRequest.newBuilder().setCache(cacheName).setKey(key)
        .setOldValue(ByteString.copyFrom(oldValue)).build();
    }


    /**
     * Factory method for RemoveValueResponse.
     *
     * @param success true if the value was removed
     * @return RemoveValueResponse instance
     */
    static CacheRpc.RemoveValueResponse removeValueResponse(boolean success)
    {
        return CacheRpc.RemoveValueResponse.newBuilder().setSuccess(success).build();
    }


    /**
     * Factory method for ReplaceRequest.
     *
     * @param cacheName cache name
     * @param key       cache key
     * @param value     cache value
     * @param ttl       entry's time-to-live
     * @param returnOld the flag specifying whether to return the old value
     * @return ReplaceRequest instance
     */
    static CacheRpc.ReplaceRequest replaceRequest(String  cacheName,
                                                  String  key,
                                                  byte[]  value,
                                                  long    ttl,
                                                  boolean returnOld)
    {
        return CacheRpc.ReplaceRequest.newBuilder().setCache(cacheName).setKey(key).setValue(ByteString.copyFrom(value))
        .setTtl(ttl).setReturn(returnOld).build();
    }


    /**
     * Factory method for ReplaceResponse.
     *
     * @param value old value to return (if requested)
     * @return ReplaceResponse instance
     */
    static CacheRpc.ReplaceResponse replaceResponse(byte[] value)
    {
        CacheRpc.ReplaceResponse.Builder builder = CacheRpc.ReplaceResponse.newBuilder();

        if (value != null)
        {
            builder.setValue(ByteString.copyFrom(value));
        }

        return builder.build();
    }


    /**
     * Factory method for ReplaceValueRequest
     *
     * @param cacheName  cache name
     * @param key        cache key
     * @param oldValue   cache value
     * @param newValue   cache value
     * @param ttl        entry's time-to-live
     * @return ReplaceValueRequest instance
     */
    static CacheRpc.ReplaceValueRequest replaceValueRequest(String cacheName,
                                                            String key,
                                                            byte[] oldValue,
                                                            byte[] newValue,
                                                            long   ttl)
    {
        return CacheRpc.ReplaceValueRequest.newBuilder().setCache(cacheName).setKey(key)
        .setOldValue(ByteString.copyFrom(oldValue)).setNewValue(ByteString.copyFrom(newValue)).setTtl(ttl).build();
    }


    /**
     * Factory method for ReplaceValueResponse.
     *
     * @param success true if the value was replaced
     * @return RemoveValueResponse instance
     */
    static CacheRpc.ReplaceValueResponse replaceValueResponse(boolean success)
    {
        return CacheRpc.ReplaceValueResponse.newBuilder().setSuccess(success).build();
    }


    /**
     * Factory method for ClearRequest.
     *
     * @param cacheName     cache name
     * @return ClearRequest instance
     */
    static CacheRpc.ClearRequest clearRequest(String cacheName)
    {
        return CacheRpc.ClearRequest.newBuilder().setCache(cacheName).build();
    }


    /**
     * Factory method for MetricsRequest.
     *
     * @param cacheName     cache name (may be "" indicating  all caches)
     * @return MetricsRequest instance
     */
    static CacheRpc.MetricsRequest metricsRequest(String cacheName)
    {
        if (cacheName == null)
        {
            throw new IllegalArgumentException("Cache name cannot be null");
        }

        return CacheRpc.MetricsRequest.newBuilder().setCache(cacheName).build();
    }


    /**
     * Factory method for MetricsResponse.
     *
     * @param cacheMetrics the metrics
     * @return MetricsResponse instance
     */
    static CacheRpc.MetricsResponse metricsResponse(ServerCacheMetrics cacheMetrics)
    {
        long nCount = cacheMetrics == null ? 0L : cacheMetrics.getCount();
        long nSize  = cacheMetrics == null ? 0L : cacheMetrics.getSize();

        return CacheRpc.MetricsResponse.newBuilder().setCount(nCount).setSize(nSize).build();
    }
     

    /**
     * A empty response that can be used by any message that does not return a response.
     *
     * @return EmptyResponse instance
     */
    static CacheRpc.EmptyResponse emptyResponse()
    {
        return EMPTY;
    }
}
