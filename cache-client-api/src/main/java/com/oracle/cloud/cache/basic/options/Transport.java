/*
 * File: Transport.java
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
 * A class defining the transport options for a connection to an
 * Application Container Cloud Service (ACCS) Application Cache.
 *
 * @author Aleksandar Seovic  2016.05.20
 */
public class Transport implements SessionOption
{
    /**
     * Enum to define transport types.
     */
    public enum Type
    {
        /**
         * Indicates REST based transport.
         */
        REST,

        /**
         * Indicates GRPC based transport.
         */
        GRPC
    }


    /**
     * Defines REST transport.
     */
    private static final Transport REST = new Transport(Type.REST);

    /**
     * Defined GRPC transport.
     */
    private static final Transport GRPC = new Transport(Type.GRPC);

    /**
     * Transport type.
     */
    private Type type;


    /**
     * Constructs a transport with the given type.
     *
     * @param type the type of the transport
     */
    private Transport(Type type)
    {
        this.type = type;
    }


    /**
     * Returns a REST transport type.
     *
     * @return a REST transport type
     */
    public static Transport rest()
    {
        return REST;
    }


    /**
     * Returns a GRPC transport type (the default).
     *
     * @return a GRPC transport type (the default)
     */
    @Options.Default
    public static Transport grpc()
    {
        return GRPC;
    }

    
    @Override
    public String toString()
    {
        return "Transport{" + "type=" + type + '}';
    }


    /**
     * Returns the type of the transport.
     *
     * @return the type of the transport
     */
    public Type getType()
    {
        return type;
    }
}
