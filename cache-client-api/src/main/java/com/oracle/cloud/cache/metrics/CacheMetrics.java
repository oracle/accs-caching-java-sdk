/*
 * File: CacheMetrics.java
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

import com.codahale.metrics.MetricRegistry;

/**
 * Encapsulates all tracked metrics for a cache.<br>
 * <strong>Note:</strong> All metrics, except count and size, are
 * tracked on a per {@link com.oracle.cloud.cache.basic.Cache} basis. For example, if you
 * create have multiple calls to {@link com.oracle.cloud.cache.basic.Session}.getCache()
 * then each will have its own distinct set of metrics (except for count and size).
 *
 * @author Aleksandar Seovic  2016.07.18
 */
public class CacheMetrics
{
    /**
     * Name of the cache to capture metrics for.
     */
    private final String cacheName;

    /**
     * Snapshot for get metrics.
     */
    private final TimerSnapshot getMetrics;

    /**
     * Snapshot for put metrics.
     */
    private final TimerSnapshot putMetrics;

    /**
     * Snapshot for remove metrics.
     */
    private final TimerSnapshot removeMetrics;

    /**
     * Snapshot for load metrics.
     */
    private final TimerSnapshot loadMetrics;

    /**
     * The total number of cache hits.
     */
    private final long hitCount;

    /**
     * The total number of cache misses.
     */
    private final long missCount;

    /**
     * The total number of entries in the cache.
     */
    private final long count;

    /**
     * The total size (in bytes) of all entries in the cache.
     */
    private final long size;


    /**
     * Constructs a CacheMetrics instance for a given cache and {@link MetricRegistry}.
     *
     * @param cacheName the name of the cache
     * @param metrics   the metrics registry for the cache
     */
    public CacheMetrics(String         cacheName,
                        MetricRegistry metrics)
    {
        this.cacheName = cacheName;
        getMetrics     = new TimerSnapshot(metrics.timer("get"));
        putMetrics     = new TimerSnapshot(metrics.timer("put"));
        removeMetrics  = new TimerSnapshot(metrics.timer("remove"));
        loadMetrics    = new TimerSnapshot(metrics.timer("load"));
        hitCount       = metrics.counter("hit").getCount();
        missCount      = metrics.counter("miss").getCount();
        count          = (long) metrics.getGauges().get("count").getValue();
        size           = (long) metrics.getGauges().get("size").getValue();
    }


    /**
     * Returns the name of the cache these metrics are for.
     *
     * @return the name of the cache
     */
    public String getCacheName()
    {
        return cacheName;
    }


    /**
     * Returns the metrics for the get operation.
     *
     * @return the metrics for the get operation
     */
    public TimerSnapshot getGetMetrics()
    {
        return getMetrics;
    }


    /**
     * Returns the metrics for the put operation.
     *
     * @return the metrics for the put operation
     */
    public TimerSnapshot getPutMetrics()
    {
        return putMetrics;
    }


    /**
     * Returns the metrics for the remove operation.
     *
     * @return the metrics for the remove operation
     */
    public TimerSnapshot getRemoveMetrics()
    {
        return removeMetrics;
    }


    /**
     * Returns the metrics for the load operation.
     *
     * @return the metrics for the load operation
     */
    public TimerSnapshot getLoadMetrics()
    {
        return loadMetrics;
    }


    /**
     * Returns the total number of cache hits.
     *
     * @return the total number of cache hits
     */
    public long getHitCount()
    {
        return hitCount;
    }


    /**
     * Returns the total number of cache misses.
     *
     * @return the total number of cache misses
     */
    public long getMissCount()
    {
        return missCount;
    }


    /**
     * Returns the cache hit ratio (hits as percentage of gets).
     *
     * @return the cache hit ratio
     */
    public double getHitRatio()
    {
        long cGets = hitCount + missCount;

        return cGets == 0 ? 0.0 : (1.0 * hitCount) / cGets;
    }


    /**
     * Returns the cache miss ratio (misses as percentage of gets).
     *
     * @return the cache miss ratio
     */
    public double getMissRatio()
    {
        long cGets = hitCount + missCount;

        return cGets == 0 ? 0.0 : (1.0 * missCount) / cGets;
    }


    /**
     * Returns the total number of entries in the cache.
     *
     * @return the total number of entries in the cache
     */
    public long getCount()
    {
        return count;
    }


    /**
     * Returns the total size (in bytes) of all entries in the cache.
     *
     * @return the total size (in bytes) of all entries in the cache
     */
    public long getSize()
    {
        return size;
    }


    @Override
    public String toString()
    {
        return "CacheMetrics{" + "\n\tcache:  " + cacheName + "\n\tget:    " + getMetrics + "\n\tput:    " + putMetrics
               + "\n\tremove: " + removeMetrics + "\n\tload:   " + loadMetrics + "\n\thits:   [count = " + hitCount
               + ", ratio = " + getHitRatio() + "]" + "\n\tmisses: [count = " + missCount + ", ratio = "
               + getMissRatio() + "]" + "\n\tcount:  " + count + "\n\tsize:   " + size + "\n}";
    }
}
