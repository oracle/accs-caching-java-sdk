/*
 * File: ValueType.java
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
 * An immutable option for configuring cache value type.
 * The default type is String.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class ValueType implements CacheOption
{
    /**
     * Default ValueType if none specified.
     */
    public static final ValueType DEFAULT = new ValueType(String.class);

    /**
     * The value class type.
     */
    private final Class<?> clzType;


    /**
     * Constructs a ValueType class.
     *
     * @param clzType the value type
     */
    private ValueType(Class<?> clzType)
    {
        this.clzType = clzType;
    }


    /**
     * Creates the option with the specified value type.
     *
     * @param clzType the value type
     * @return the ValueType
     */
    public static ValueType of(Class<?> clzType)
    {
        return new ValueType(clzType);
    }


    /**
     * Specifies that the default type (String) should be used.
     *
     * @return the ValueType
     */
    @Options.Default
    public static ValueType defaultType()
    {
        return DEFAULT;
    }


    /**
     * Returns the value type.
     *
     * @return the value type
     */
    public Class<?> getType()
    {
        return clzType;
    }
}
