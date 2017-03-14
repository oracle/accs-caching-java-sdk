/*
 * File: CacheLoader.java
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

import com.oracle.cloud.cache.basic.options.CacheOption;

/**
 * <p>
 * A CacheLoader allows logic to be defined and called (on the client) on a cache-miss:
 * such as when there is no value associated with a given key.  In this scenario, the load()
 * method is called and the resulting value is associated with the key and returned to the caller.
 * </p>
 * <p>
 * The cache loader can be a class that implements the {@link CacheLoader} interface or
 * a Lambda and is useful for implementing the "cache-aside" pattern.
 * </p>
 *
 * Example:
 * <pre>
 * Session session = new RemoteSessionProvider(....).createSession();
 *
 * Cache&lt;String&gt; cache = session.getCache("my-cache",
 *                            CacheLoader.of(key -&gt;
 *                            {
 *                            String value = ... // load value from some location
 *                            return value;
 *                            })
 *                        );
 * </pre>
 *
 * @author Aleksandar Seovic  2016.06.02
 */
@FunctionalInterface
public interface CacheLoader<V> extends CacheOption
{
    /**
     * On a cache miss, this method is called with the given key to load the cache value.
     *
     * @param key the key to load
     * @return the cache value (can be null)
     */
    V load(String key);


    /**
     * Helper method to specify a CacheLoader to employ in the event of a cache miss.
     *
     * @param loader cache loader to use
     * @param <V>    value type that the cache load will return
     * @return the cache loader
     */
    static <V> CacheLoader<V> of(CacheLoader<V> loader)
    {
        return loader;
    }
}
