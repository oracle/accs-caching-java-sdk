/*
 * File: ServerCacheMetrics.java
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

/**
 * Encapsulates server-side metrics for the cache.
 *
 * @author Aleksandar Seovic  2016.07.18
 */
public class ServerCacheMetrics
{
    /**
     * The total number of entries in the cache.
     */
    private long count;

    /**
     * The total size (in bytes) of all entries in the cache.
     */
    private long size;


    /**
     * Constructs a ServerMetrics instance.
     */
    public ServerCacheMetrics()
    {
    }


    /**
     * Constructs a ServerMetrics instance.
     *
     * @param count  the total number of entries in the cache
     * @param size   the total size (in bytes) of all entries in the cache
     */
    public ServerCacheMetrics(long count,
                              long size)
    {
        this.count = count;
        this.size  = size;
    }


    @Override
    public String toString()
    {
        return "ServerCacheMetrics{" + "count=" + count + ", size=" + size + '}';
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ServerCacheMetrics metrics = (ServerCacheMetrics) o;

        if (count != metrics.count)
        {
            return false;
        }

        return size == metrics.size;

    }


    @Override
    public int hashCode()
    {
        int result = (int) (count ^ (count >>> 32));

        result = 31 * result + (int) (size ^ (size >>> 32));

        return result;
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


    /**
     * Increments the count by the specified delta.
     *
     * @param countDelta the number to increment count by
     */
    public void incrementCount(long countDelta)
    {
        count += countDelta;
    }


    /**
     * Increments the size by the specified delta.
     *
     * @param sizeDelta the number to increment size by
     */
    public void incrementSize(long sizeDelta)
    {
        size += sizeDelta;
    }
}
