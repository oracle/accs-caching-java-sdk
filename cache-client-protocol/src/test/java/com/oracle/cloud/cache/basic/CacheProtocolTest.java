/*
 * File: CacheProtocolTest.java
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

import com.oracle.cloud.cache.ServerCacheMetrics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
                                            
/**
 * Tests for CacheProtocol.
 *
 * @author Tim Middleton  2016.12.09
 */
public class CacheProtocolTest
{
    /**
     * Test individual {@link CacheProtocol} messages.
     */
    @Test
    public void testCacheProtocolMessages()
    {
        // test get request
        CacheRpc.GetRequest getRequest = CacheProtocol.getRequest("cache", "key");

        assertEquals("cache", getRequest.getCache());
        assertEquals("key", getRequest.getKey());

        // test empty get response
        CacheRpc.GetResponse getResponse = CacheProtocol.getResponse(null);

        assertEquals(0, getResponse.getValue().size());

        // test response withing some value
        getResponse = CacheProtocol.getResponse("response".getBytes());
        assertEquals("response", getResponse.getValue().toStringUtf8());

        String value        = "the value";
        byte[] valueAsBytes = value.getBytes();

        // test put request
        CacheRpc.PutRequest putRequest = CacheProtocol.putRequest("cache", "key", valueAsBytes, 101L, false);

        assertEquals("cache", putRequest.getCache());
        assertEquals("key", putRequest.getKey());
        assertEquals(value, putRequest.getValue().toStringUtf8());
        assertEquals(101L, putRequest.getTtl());
        assertEquals(false, putRequest.getReturn());

        // test empty put response
        CacheRpc.PutResponse putResponse = CacheProtocol.putResponse(null);

        assertEquals(0, putResponse.getValue().size());

        // test put response withing some value
        putResponse = CacheProtocol.putResponse("put-response".getBytes());
        assertEquals("put-response", putResponse.getValue().toStringUtf8());

        // test remove request
        CacheRpc.RemoveRequest removeRequest = CacheProtocol.removeRequest("cache", "key", true);

        assertEquals("cache", removeRequest.getCache());
        assertEquals("key", removeRequest.getKey());
        assertEquals(true, removeRequest.getReturn());

        // test empty remove response
        CacheRpc.RemoveResponse removeResponse = CacheProtocol.removeResponse(null);

        assertEquals(0, removeResponse.getValue().size());

        // test remove response withing some value
        removeResponse = CacheProtocol.removeResponse("remove-response".getBytes());
        assertEquals("remove-response", removeResponse.getValue().toStringUtf8());

        // test remove value request
        CacheRpc.RemoveValueRequest removeValueRequest = CacheProtocol.removeValueRequest("cache", "key", valueAsBytes);

        assertEquals("cache", removeValueRequest.getCache());
        assertEquals("key", removeValueRequest.getKey());
        assertEquals(value, removeValueRequest.getOldValue().toStringUtf8());

        // test remove value response
        CacheRpc.RemoveValueResponse removeValueResponse = CacheProtocol.removeValueResponse(true);

        assertEquals(true, removeValueResponse.getSuccess());

        // test replace request
        CacheRpc.ReplaceRequest replaceRequest = CacheProtocol.replaceRequest("cache", "key", valueAsBytes, -1L, false);

        assertEquals("cache", replaceRequest.getCache());
        assertEquals("key", replaceRequest.getKey());
        assertEquals(value, replaceRequest.getValue().toStringUtf8());
        assertEquals(-1L, replaceRequest.getTtl());
        assertEquals(false, replaceRequest.getReturn());

        // test empty replace response
        CacheRpc.ReplaceResponse replaceResponse = CacheProtocol.replaceResponse(null);

        assertEquals(0, replaceResponse.getValue().size());

        // test replace response withing some value
        replaceResponse = CacheProtocol.replaceResponse("replace-response".getBytes());
        assertEquals("replace-response", replaceResponse.getValue().toStringUtf8());

        String newValue        = "new-value";
        byte[] newValueAsBytes = newValue.getBytes();

        // test replace value request
        CacheRpc.ReplaceValueRequest replaceValueRequest = CacheProtocol.replaceValueRequest("cache",
                                                                                             "key",
                                                                                             valueAsBytes,
                                                                                             newValueAsBytes,
                                                                                             1000L);

        assertEquals("cache", replaceValueRequest.getCache());
        assertEquals("key", replaceValueRequest.getKey());
        assertEquals(value, replaceValueRequest.getOldValue().toStringUtf8());
        assertEquals(newValue, replaceValueRequest.getNewValue().toStringUtf8());
        assertEquals(1000L, replaceValueRequest.getTtl());

        // test replace value response
        CacheRpc.ReplaceValueResponse replaceValueResponse = CacheProtocol.replaceValueResponse(false);

        assertEquals(false, replaceValueResponse.getSuccess());

        // test clear request
        CacheRpc.ClearRequest clearRequest = CacheProtocol.clearRequest("cache");

        assertEquals("cache", clearRequest.getCache());

        // test metrics request
        CacheRpc.MetricsRequest metricsRequest = CacheProtocol.metricsRequest("cache");

        assertEquals("cache", metricsRequest.getCache());

        // test metrics response
        ServerCacheMetrics       metrics         = new ServerCacheMetrics(1L, 1000L);
        CacheRpc.MetricsResponse metricsResponse = CacheProtocol.metricsResponse(metrics);

        assertEquals(1L, metricsResponse.getCount());
        assertEquals(1000L, metricsResponse.getSize());

        // test metrics response will null ServerCacheMetrics
        metricsResponse = CacheProtocol.metricsResponse(null);
        assertEquals(0L, metricsResponse.getCount());
        assertEquals(0L, metricsResponse.getSize());
    }
}
