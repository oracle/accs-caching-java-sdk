/*
 * File: Expiry.java
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

package com.oracle.cloud.cache.basic.options;

import com.oracle.cloud.cache.util.Options;

import java.time.Duration;

import java.util.concurrent.TimeUnit;

/**
 * An immutable option for configuring cache entry expiry.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class Expiry implements CacheOption, PutOption, ReplaceOption, GetOption
{
    /**
     * The cache entry expiration in milliseconds.
     */
    private final long expiry;


    /**
     * Constructs the expiry option with the specified duration of the cache
     * entry.
     *
     * @param expiry the entry expiry in milliseconds, or one of the following
     *               two special values:
     *               0 - to use default expiry for the cache, or
     *               -1 - to never expire
     */
    private Expiry(long expiry)
    {
        this.expiry = expiry;
    }


    /**
     * Returns the cache entry expiration in milliseconds.
     *
     * @return the cache entry expiration in milliseconds
     */
    public long getExpiry()
    {
        return expiry;
    }


    /**
     * Creates an expiry option with the specified time-to-live of the cache
     * entry utilizing a {@link TimeUnit} to specify the scale of the expiry.
     *
     * @param ttl  the entry TTL
     * @param unit the TimeUnit of the TTL
     * @return the Expiry
     */
    public static Expiry of(long     ttl,
                            TimeUnit unit)
    {
        return new Expiry(unit.toMillis(ttl));
    }


    /**
     * Creates an expiry option with the specified time-to-live for the cache entry.
     *
     * @param duration the entry duration
     * @return the Expiry
     */
    public static Expiry of(Duration duration)
    {
        return new Expiry(duration.toMillis());
    }


    /**
     * Specifies that the entry should never expire.
     *
     * @return the Expiry
     */
    public static Expiry never()
    {
        return new Expiry(-1L);
    }


    /**
     * Specifies that the default expiry should be used.
     *
     * @return the Expiry
     */
    @Options.Default
    public static Expiry defaultExpiry()
    {
        return new Expiry(0L);
    }
}
