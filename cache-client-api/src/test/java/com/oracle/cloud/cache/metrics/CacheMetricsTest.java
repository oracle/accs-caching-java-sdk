/*
 * File: CacheMetricsTest.java
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

package com.oracle.cloud.cache.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CacheMetrics}.
 *
 * @author Tim Middleton  2016.10.24
 */
public class CacheMetricsTest
{
    /**
     * Test basic metrics.
     */
    @Test
    public void testBasicConstruction()
    {
        String       cacheName = "my-cache";

        CacheMetrics metrics   = new CacheMetrics(cacheName, getMetricRegistry());

        assertEquals(cacheName, metrics.getCacheName());
        assertEquals(metrics.getCount(), 0L);
        assertEquals(metrics.getSize(), 0L);
        assertEquals(metrics.getMissCount(), 0L);
    }


    /**
     * Returns a {@link MetricRegistry} for testing.
     *
     * @return a new {@link MetricRegistry}
     */
    private MetricRegistry getMetricRegistry()
    {
        MetricRegistry metricRegistry = new MetricRegistry();

        metricRegistry.register("get", new Timer());
        metricRegistry.register("put", new Timer());
        metricRegistry.register("remove", new Timer());
        metricRegistry.register("load", new Timer());
        metricRegistry.register("hit", new Counter());
        metricRegistry.register("miss", new Counter());
        metricRegistry.register("count", (Gauge<Long>) () -> 0L);
        metricRegistry.register("size", (Gauge<Long>) () -> 0L);

        return metricRegistry;
    }
}
 