/*
 * File: Session.java
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
 * A session to the Application Container Cloud Service (ACCS) Application Cache.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public interface Session
{
    /**
     * Returns an instance of a cache with the given name and options. If the cache does not exist, it is created.
     *
     * @param <V>       the type of cache values, as determined by the
     *                  {@link com.oracle.cloud.cache.basic.options.ValueType} cache option (String by default)
     * @param cacheName the name of the cache, which is unique for a given
     *                  ACCS instance
     * @param options   the cache options
     *
     * @return the cache
     */
    <V> Cache<V> getCache(String         cacheName,
                          CacheOption... options);
}
