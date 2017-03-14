/*
 * File: Return.java
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

/**
 * A class defining the return options for cache operations.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class Return implements PutOption, ReplaceOption, RemoveOption
{
    /**
     * Indicates to return nothing.
     */
    public static final Return NOTHING = new Return(false);

    /**
     * Indicates to return the old value.
     */
    public static final Return OLD_VALUE = new Return(true);

    /**
     * Indicates if a value should be returned from the cache operation.
     */
    private boolean value;


    /**
     * Constructs a Return object with the give value.
     *
     * @param value indicates if a value should be returned from
     *              the cache operation
     */
    private Return(boolean value)
    {
        this.value = value;
    }


    /**
     * Indicates if a value should be returned from the cache operation.
     *
     * @return true of false
     */
    public boolean value()
    {
        return value;
    }


    /**
     * Returns an option indicating to return nothing.
     *
     * @return an option indicating to return nothing
     */
    @Options.Default
    public static Return nothing()
    {
        return NOTHING;
    }


    /**
     * Returns an option indicating to return the old value.
     *
     * @return an option indicating to return old value
     */
    public static Return oldValue()
    {
        return OLD_VALUE;
    }
}
