/*
 * File: AbstractCache.java
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

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.oracle.cloud.cache.ServerCacheMetrics;
import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.Expiry;
import com.oracle.cloud.cache.basic.options.GetOption;
import com.oracle.cloud.cache.basic.options.PutOption;
import com.oracle.cloud.cache.basic.options.RemoveOption;
import com.oracle.cloud.cache.basic.options.ReplaceOption;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.basic.options.ValueType;
import com.oracle.cloud.cache.metrics.CacheMetrics;
import com.oracle.cloud.cache.util.Options;

/**                                        
 * Abstract implementation of {@link Cache}, which provides base functionality
 * and helpers for use by other implementations.
 *
 * @param <V> Value type of the cache
 *
 * @author Aleksandar Seovic  2016.06.17
 */
public abstract class AbstractCache<V> implements Cache<V>
{
    /**
     * Cache name.
     */
    private final String cacheName; 

    /**
     * Metric registry.
     */
    private MetricRegistry metrics;

    /**
     * Any options applied.
     */
    private final Options<CacheOption> options;

    /**
     * The value class for the cache.
     */
    private final Class<V> valueClass;

    /**
     * The {@link CacheLoader} for the cache or null if none is defined.
     */
    private final CacheLoader<V> cacheLoader;


    /**
     * Constructor for AbstractCache based upon cache name and {@link CacheOption}s.
     *
     * @param cacheName  cache name
     * @param options    options to apply to the cache
     */
    @SuppressWarnings("unchecked")
    protected AbstractCache(String         cacheName,
                            CacheOption... options)
    {
        this.cacheName   = cacheName;
        this.metrics     = createMetrics();
        this.options     = Options.from(CacheOption.class, options);
        this.valueClass  = (Class<V>) this.options.get(ValueType.class).getType();
        this.cacheLoader = this.options.get(CacheLoader.class);
    }


    /**
     * Returns the value in the cache identified by the specified key. Returns
     * null if the cache contains no mapping for this key.
     *
     * @param key  the cache key
     *
     * @return the value associated with the specified key
     */
    protected abstract V get(String key);


    /**
     * Associates the specified value with the specified key. If the cache
     * previously contained a value for this key, the old value is replaced.
     *
     * @param key         the cache key
     * @param value       the value to associate with the specified key
     * @param expiry      time-to-live for the cache entry
     * @param returnOld  flag specifying whether to return previous value
     *
     * @return the previous value, if {@code returnOld} is true; {@code null} otherwise
     */
    protected abstract V put(String  key,
                             V       value,
                             Expiry  expiry,
                             boolean returnOld);


    /**
     * If the specified key is not already associated with a value (or is mapped
     * to null), associates it with the given value.
     *
     * @param key         the cache key
     * @param value       the value to associate with the specified key
     * @param expiry      time-to-live for the cache entry
     * @param returnOld  flag specifying whether to return previous value
     *
     * @return the current value, if {@code returnOld} is true and there is a value
     *         associated with the specified key; {@code null} otherwise
     */
    protected abstract V putIfAbsent(String  key,
                                     V       value,
                                     Expiry  expiry,
                                     boolean returnOld);


    /**
     * Replaces the entry for the specified key if the key is currently
     * mapped to some value.
     *
     * @param key         the cache key
     * @param value       the value to associate with the specified key
     * @param expiry      time-to-live for the cache entry
     * @param returnOld  flag specifying whether to return previous value
     *
     * @return the previous value, if {@code returnOld} is true and there is a value
     *         associated with the specified key; {@code null} otherwise
     */
    protected abstract V replace(String  key,
                                 V       value,
                                 Expiry  expiry,
                                 boolean returnOld);


    /**
     * Replaces the entry for the specified key if the key is currently
     * mapped to the specified old value.
     *
     * @param key       the cache key
     * @param valueOld  the value to match against the current cached value
     * @param valueNew  the value to associate with the specified key
     * @param expiry    time-to-live for the cache entry
     *
     * @return a boolean specifying whether the value was replaced
     */
    protected abstract boolean replaceValue(String key,
                                            V      valueOld,
                                            V      valueNew,
                                            Expiry expiry);


    /**
     * Removes the mapping for a key from this cache if it is present.
     *
     * @param key         the cache key
     * @param returnOld  flag specifying whether to return previous value
     *
     * @return the previous value, if {@code returnOld} is true and there is a value
     *         associated with the specified key; {@code null} otherwise
     */
    protected abstract V remove(String  key,
                                boolean returnOld);


    /**
     * Removes the entry for the specified key if the key is currently
     * mapped to the specified value.
     *
     * @param key    the cache key
     * @param value  the value to match against the current cached value
     *
     * @return the previous value, if {@code returnOld} is true and there is a value
     *         associated with the specified key; {@code null} otherwise
     */
    protected abstract boolean removeValue(String key,
                                           V      value);


    /**
     * Returns the server metrics for this cache.
     *
     * @return the server metrics for this cache
     */
    protected abstract ServerCacheMetrics getServerMetrics();


    @Override
    public V get(String       key,
                 GetOption... options)
    {
        // get options for use in cache loader on miss
        Options<GetOption> opts      = Options.from(GetOption.class, options);
        Expiry             expiry    = opts.get(Expiry.class, this.options.get(Expiry.class, Expiry.never()));
        long               startTime = System.nanoTime();

        V                  value     = get(key);

        if (value == null)
        {
            // Cache Miss
            registerMiss(startTime);

            // issue request to cache loader if one exists
            if (cacheLoader != null)
            {
                startTime = System.nanoTime();
                value     = cacheLoader.load(key);
                registerLoad(startTime);

                if (value != null)
                {
                    V currentValue = putIfAbsent(key, value, expiry, Return.oldValue());

                    return currentValue == null ? value : currentValue;
                }
            }

            return null;
        }
        else
        {
            registerHit(startTime);

            return value;
        }
    }


    @Override
    public V put(String       key,
                 V            value,
                 PutOption... options)
    {
        Options<PutOption> opts      = Options.from(PutOption.class, options);
        Expiry             expiry    = opts.get(Expiry.class, this.options.get(Expiry.class, Expiry.never()));
        boolean            returnOld = opts.get(Return.class).value();
        long               startTime = System.nanoTime();

        V                  oldValue  = put(key, value, expiry, returnOld);

        registerPut(startTime);

        return oldValue;
    }


    @Override
    public V putIfAbsent(String       key,
                         V            value,
                         PutOption... options)
    {
        Options<PutOption> opts         = Options.from(PutOption.class, options);
        Expiry             expiry       = opts.get(Expiry.class, this.options.get(Expiry.class, Expiry.never()));
        boolean            returnOld    = opts.get(Return.class).value();
        long               startTime    = System.nanoTime();

        V                  currentValue = putIfAbsent(key, value, expiry, returnOld);

        registerPut(startTime);

        return currentValue;
    }


    @Override
    public V replace(String           key,
                     V                value,
                     ReplaceOption... options)
    {
        Options<ReplaceOption> opts      = Options.from(ReplaceOption.class, options);
        Expiry                 expiry    = opts.get(Expiry.class, this.options.get(Expiry.class, Expiry.never()));
        boolean                returnOld = opts.get(Return.class).value();

        long                   startTime = System.nanoTime();

        V                      oldValue  = replace(key, value, expiry, returnOld);

        registerPut(startTime);

        return oldValue;
    }


    @Override
    public boolean replace(String           key,
                           V                valueOld,
                           V                valueNew,
                           ReplaceOption... options)
    {
        Options<ReplaceOption> opts      = Options.from(ReplaceOption.class, options);
        Expiry                 expiry    = opts.get(Expiry.class, this.options.get(Expiry.class, Expiry.never()));
        long                   startTime = System.nanoTime();

        boolean                replaced = replaceValue(key, valueOld, valueNew, expiry);

        registerPut(startTime);

        return replaced;
    }


    @Override
    public V remove(String          key,
                    RemoveOption... options)
    {
        Options<RemoveOption> opts      = Options.from(RemoveOption.class, options);
        boolean               returnOld = opts.get(Return.class).value();
        long                  startTime = System.nanoTime();

        V                     oldValue  = remove(key, returnOld);

        registerRemove(startTime);

        return oldValue;
    }


    @Override
    public boolean remove(String          key,
                          V               value,
                          RemoveOption... options)
    {
        long    startTime = System.nanoTime();

        boolean fRemoved = removeValue(key, value);

        registerRemove(startTime);

        return fRemoved;
    }


    @Override
    public CacheMetrics getMetrics()
    {
        return new CacheMetrics(getCacheName(), metrics);
    }


    @Override
    public void resetMetrics()
    {
        metrics = createMetrics();
    }


    /**
     * Returns the cache name.
     *
     * @return the cache name.
     */
    protected String getCacheName()
    {
        return cacheName;
    }


    /**
     * Returns the value class for the cache.
     *
     * @return the value class for the cache
     */
    protected Class<V> getValueClass()
    {
        return valueClass;
    }


    /**
     * Returns the options for the cache.
     *
     * @return the options for the cache.
     */
    protected Options<CacheOption> getOptions()
    {
        return options;
    }


    /**
     * Creates a new {@link MetricRegistry} to record various metrics regarding
     * cache access for this cache.
     *
     * @return  {@link MetricRegistry} instance
     */
    protected MetricRegistry createMetrics()
    {
        MetricRegistry metrics = new MetricRegistry();

        metrics.register("get", new Timer());
        metrics.register("put", new Timer());
        metrics.register("remove", new Timer());
        metrics.register("load", new Timer());
        metrics.register("hit", new Counter());
        metrics.register("miss", new Counter());
        metrics.register("count", (Gauge<Long>) () -> getServerMetrics().getCount());
        metrics.register("size", (Gauge<Long>) () -> getServerMetrics().getSize());

        return metrics;
    }


    /**
     * Registers a hit on the cache.
     *
     * @param startTime  the start time of the request
     */
    protected void registerHit(long startTime)
    {
        long duration = duration(startTime);

        metrics.timer("get").update(duration, TimeUnit.NANOSECONDS);
        metrics.counter("hit").inc();
    }


    /**
     * Registers a miss on the cache.
     *
     * @param startTime  the start time of the request
     */
    protected void registerMiss(long startTime)
    {
        long duration = duration(startTime);

        metrics.timer("get").update(duration, TimeUnit.NANOSECONDS);
        metrics.counter("miss").inc();
    }


    /**
     * Registers a put on the cache.
     *
     * @param startTime  the start time of the request
     */
    protected void registerPut(long startTime)
    {
        metrics.timer("put").update(duration(startTime), TimeUnit.NANOSECONDS);
    }


    /**
     * Registers a remove on the cache.
     *
     * @param startTime  the start time of the request
     */
    protected void registerRemove(long startTime)
    {
        metrics.timer("remove").update(duration(startTime), TimeUnit.NANOSECONDS);
    }


    /**
     * Registers a load on the cache.
     *
     * @param startTime  the start time of the request
     */
    protected void registerLoad(long startTime)
    {
        metrics.timer("load").update(duration(startTime), TimeUnit.NANOSECONDS);
    }


    /**
     * Calculates duration based on start time.
     *
     * @param startTime  the start time of the operation
     *
     * @return the duration of the operation in nanoseconds
     */
    protected long duration(long startTime)
    {
        return Math.max(0, System.nanoTime() - startTime);
    }
}
