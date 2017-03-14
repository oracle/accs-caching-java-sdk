/*
 * File: Cache.java
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

import com.oracle.cloud.cache.basic.options.GetOption;
import com.oracle.cloud.cache.basic.options.PutOption;
import com.oracle.cloud.cache.basic.options.RemoveOption;
import com.oracle.cloud.cache.basic.options.ReplaceOption;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.metrics.CacheMetrics;

/**
 * An interface that defines the operations that can be
 * performed against a cache.
 *
 * @param <V> the type of values to be stored within the cache
 * @author Aleksandar Seovic  2016.05.20
 */
public interface Cache<V>
{
    /**
     * Returns the value in the cache identified by the specified key. Returns
     * null if the cache contains no mapping for this key.
     *
     * @param key     the key which identifies the cache entry
     * @param options the options for this operation (only required when {@link CacheLoader} used)
     * @return the cache value
     * @throws NullPointerException if the specified key is null
     */
    V get(String       key,
          GetOption... options);


    /**
     * Associates the specified value with the specified key. If the cache
     * previously contained a value for this key, the old value is replaced.
     *
     * @param key     the key which identifies the cache entry
     * @param value   the cache entry value
     * @param options the options for this operation
     * @return {@code null}, unless {@link Return} option is specified
     * @throws NullPointerException if the specified value or key is null
     */
    V put(String       key,
          V            value,
          PutOption... options);


    /**
     * If the specified key is not already associated with a value (or is mapped
     * to null), associates it with the given value.
     *
     * @param key     the key which identifies the cache entry
     * @param value   the cache entry value
     * @param options the options for this operation
     * @return {@code null}, unless {@link Return#oldValue()} option is specified then the old value is returned
     * @throws NullPointerException if the specified value or key is null
     */
    V putIfAbsent(String       key,
                  V            value,
                  PutOption... options);


    /**
     * Replaces the entry for the specified key if the key is currently
     * mapped to some value.
     *
     * @param key     the key which identifies the cache entry
     * @param value   the new cache entry value
     * @param options the options for this operation
     * @return {@code null}, unless {@link Return#oldValue()} option is specified then the old value is returned
     * and the mapping is already present in the cache.
     * @throws NullPointerException if the specified value or key is null
     */
    V replace(String           key,
              V                value,
              ReplaceOption... options);


    /**
     * Replaces the entry for the specified key if the key is currently
     * mapped to the specified old value.
     *
     * @param key      the key which identifies the cache entry
     * @param valueOld the existing cache entry value
     * @param valueNew the new cache entry value
     * @param options  the options for this operation
     * @return true if the entry was replaced
     * @throws NullPointerException if the specified value or key is null
     */
    boolean replace(String           key,
                    V                valueOld,
                    V                valueNew,
                    ReplaceOption... options);


    /**
     * Removes the mapping for a key from this cache if it is present.
     *
     * @param key     the key which identifies the cache entry
     * @param options the options for this operation
     * @return {@code null}, unless {@link Return#oldValue()} option is specified then the old value is returned
     * @throws NullPointerException if the specified key is null
     */
    V remove(String          key,
             RemoveOption... options);


    /**
     * Removes the entry for the specified key if the key is currently
     * mapped to the specified value.
     *
     * @param key     the key which identifies the cache entry
     * @param value   the cache entry value
     * @param options the options for this operation
     * @return true if the entry was removed from the cache
     * @throws NullPointerException if the specified value or key is null
     */
    boolean remove(String          key,
                   V               value,
                   RemoveOption... options);


    /**
     * Clears all entries from the cache.
     */
    void clear();


    /**
     * Returns the metrics associated with this cache.
     *
     * @return a {@link CacheMetrics} object containing the requested metrics
     */
    CacheMetrics getMetrics();


    /**
     * Resets the metrics associated with this cache. <br>
     * <strong>Note:</strong> Only timing metrics are reset, not count and size.
     */
    void resetMetrics();
}
