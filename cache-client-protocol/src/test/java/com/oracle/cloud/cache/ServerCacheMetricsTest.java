/*
 * File: ServerCacheMetricsTest.java
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

package com.oracle.cloud.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for ServerCacheMetric.
 *
 * @author Tim Middleton  2016.12.09
 */
public class ServerCacheMetricsTest
{
    /**
     * Tests for {@link ServerCacheMetrics}.
     */
    @Test
    public void testServerCacheMetric()
    {
        ServerCacheMetrics metrics = new ServerCacheMetrics();

        assertEquals(0L, metrics.getCount());
        assertEquals(0L, metrics.getSize());

        metrics.incrementCount(10L);
        assertEquals(10L, metrics.getCount());

        metrics.incrementSize(2222L);
        assertEquals(2222L, metrics.getSize());

        metrics = new ServerCacheMetrics(1L, 200L);
        assertEquals(1L, metrics.getCount());
        assertEquals(200L, metrics.getSize());

        ServerCacheMetrics metrics1 = new ServerCacheMetrics(1L, 200L);

        assertTrue(metrics.equals(metrics1));
    }
}
