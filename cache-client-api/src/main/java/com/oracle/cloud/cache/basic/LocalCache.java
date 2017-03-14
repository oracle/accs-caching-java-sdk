/*
 * File: LocalCache.java
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.oracle.cloud.cache.ServerCacheMetrics;
import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.Expiry;

/**
 * An implementation of the {@link Cache} interface which uses
 * a local {@link ConcurrentHashMap} to store cache entries. Useful for
 * local testing locally without having to deploy into Application Container Cloud Service (ACCS).
 *
 * @param <V> value type for the cache
 * @author Aleksandar Seovic/Tim Middleton  2016.06.02
 */
public class LocalCache<V> extends AbstractCache<V>
{
    /**
     * Indicates no expiry.
     */
    private static final long NO_EXPIRY = -1L;

    /**
     * {@link ConcurrentHashMap} to store the actual local cache contents.
     */
    private final ConcurrentHashMap<String, ExpiringValue<V>> mapLocalCache = new ConcurrentHashMap<>();

    /**
     * The time before which a expired-entries flush will not be performed.
     */
    private long nextFlush = 0L;

    /**
     * The {@link LocalSession} that created this cache.
     */
    private final LocalSession session;


    /**
     * Constructs a LocalCache for the given cache and session.
     *
     * @param cacheName cache name
     * @param session   the {@link Session} that created this LocalCache
     * @param options   any option to apply
     */
    LocalCache(String         cacheName,
               LocalSession   session,
               CacheOption... options)
    {
        super(cacheName, options);

        this.session = session;
    }


    @Override
    protected V get(String key)
    {
        evict();

        ExpiringValue<V> expiringValue = mapLocalCache.get(key);

        return expiringValue == null ? null : expiringValue.getValue();
    }


    @Override
    protected V put(String  key,
                    V       value,
                    Expiry  expiry,
                    boolean returnOld)
    {
        evict();

        ExpiringValue<V> oldValue = mapLocalCache.put(key, new ExpiringValue<>(value, expiry.getExpiry()));

        return returnOld ? (oldValue == null ? null : oldValue.getValue()) : null;
    }


    @Override
    protected V putIfAbsent(String  key,
                            V       value,
                            Expiry  expiry,
                            boolean returnOld)
    {
        evict();

        ExpiringValue<V> oldValue = mapLocalCache.putIfAbsent(key, new ExpiringValue<>(value, expiry.getExpiry()));

        return returnOld ? (oldValue == null ? null : oldValue.getValue()) : null;
    }


    @Override
    protected V replace(String  key,
                        V       value,
                        Expiry  expiry,
                        boolean returnOld)
    {
        evict();

        ExpiringValue<V> oldValue = mapLocalCache.replace(key, new ExpiringValue<>(value, expiry.getExpiry()));

        return returnOld ? (oldValue == null ? null : oldValue.getValue()) : null;
    }


    @Override
    protected boolean replaceValue(String key,
                                   V      valueOld,
                                   V      valueNew,
                                   Expiry expiry)
    {
        evict();

        long expiryTime = expiry.getExpiry();

        return mapLocalCache.replace(key,
                                     new ExpiringValue<>(valueOld, expiryTime),
                                     new ExpiringValue<>(valueNew, expiryTime));
    }


    @Override
    protected V remove(String  key,
                       boolean returnOld)
    {
        evict();

        ExpiringValue<V> oldValue = mapLocalCache.remove(key);

        return returnOld ? (oldValue == null ? null : oldValue.getValue()) : null;
    }


    @Override
    protected boolean removeValue(String key,
                                  V      value)
    {
        return mapLocalCache.remove(key, new ExpiringValue<>(value, 0L));
    }


    @Override
    protected ServerCacheMetrics getServerMetrics()
    {
        return new ServerCacheMetrics(mapLocalCache.size(), -1L);
    }


    @Override
    public void clear()
    {
        synchronized (this)
        {
            mapLocalCache.clear();
        }
    }


    /**
     * Evict any entries that have reached expiry time.
     */
    private void evict()
    {
        long current = System.currentTimeMillis();

        synchronized (this)
        {
            // protect against other threads attempting to evict() at the same time
            if (current > nextFlush)
            {
                nextFlush = System.currentTimeMillis();

                try
                {
                    Set<String> entries = new HashSet<>();

                    mapLocalCache.forEach(
                        (k, v) -> {
                            long expiryTime = v.getExpiry();

                            if (expiryTime != NO_EXPIRY && expiryTime < System.currentTimeMillis())
                            {
                                entries.add(k);
                            }
                        });

                    // remove the entries
                    entries.forEach((k) -> mapLocalCache.remove(k));
                }
                finally
                {
                    // don't allow another flush for 50ms
                    nextFlush = System.currentTimeMillis() + 50L;
                }

            }
        }
    }


    /**
     * Inner class to hold the value plus absolute expiry or -1 if no expiry.
     *
     * @param <V> value to store
     */
    private static class ExpiringValue<V>
    {
        /**
         * The value to store in the cache.
         */
        private final V value;

        /**
         * The absolute expiry time or -1 if no expiry.
         */
        private final long expiryTime;


        /**
         * Constructs a new ExpiringValue with the value and expiry time.
         *
         * @param value     value to store
         * @param expiryTime absolute expiry time in millis
         */
        private ExpiringValue(V    value,
                              long expiryTime)
        {
            this.value      = value;
            this.expiryTime = expiryTime;
        }


        /**
         * Creates a new expiring value and set the expiry to the absolute expiry or
         * -1L if expiry is not required.
         *
         * @param value     the value to
         * @param expiryTime Expiry value in millis or -1L for no expiry
         * @return ExpiringValue entry
         */
        public ExpiringValue of(V    value,
                                long expiryTime)
        {
            return new ExpiringValue(value,
                                     expiryTime == NO_EXPIRY ? NO_EXPIRY : System.currentTimeMillis() + expiryTime);
        }


        /**
         * Returns the value of the entry.
         *
         * @return the value of the entry
         */
        public V getValue()
        {
            return value;
        }


        /**
         * Returns the expiry of the entry.
         *
         * @return the expiry of the entry
         */
        public long getExpiry()
        {
            return expiryTime;
        }


        /**
         * Comparison is done only on value not ttl as ttl is not relevant.
         *
         * @param o object to compare
         * @return true if the objects values equal
         */
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

            ExpiringValue<?> that = (ExpiringValue<?>) o;

            return value != null ? value.equals(that.value) : that.value == null;
        }


        @Override
        public String toString()
        {
            return "ExpiringValue{" + "value=" + value + ", expiryTime=" + expiryTime + '}';
        }


        @Override
        public int hashCode()
        {
            return value != null ? value.hashCode() : 0;
        }
    }
}
