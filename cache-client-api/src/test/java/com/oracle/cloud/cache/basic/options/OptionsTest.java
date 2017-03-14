/*
 * File: OptionsTest.java
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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.oracle.cloud.cache.util.Options;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link Options} implementations.
 *
 * @author Tim Middleton  2016.10.24
 */
public class OptionsTest
{
    /**
     * Test {@link Expiry} option.
     */
    @Test
    public void testExpiryOption()
    {
        Expiry expiry = Expiry.defaultExpiry();

        assertEquals(0L, expiry.getExpiry());

        expiry = Expiry.of(Duration.ZERO);
        assertEquals(0L, expiry.getExpiry());

        expiry = Expiry.of(10, TimeUnit.MILLISECONDS);
        assertEquals(10, expiry.getExpiry());

        expiry = Expiry.of(10, TimeUnit.MINUTES);
        assertEquals(10 * 60 * 1000, expiry.getExpiry());
    }


    /**
     * Test {@link Return} option.
     */
    @Test
    public void testReturnOption()
    {
        Return returnOption = Return.NOTHING;

        assertEquals(false, returnOption.value());

        returnOption = Return.OLD_VALUE;
        assertEquals(true, returnOption.value());
    }


    /**
     * Test {@link Transport} option.
     */
    @Test
    public void testTransportOption()
    {
        Transport transport = Transport.grpc();

        assertEquals(Transport.Type.GRPC, transport.getType());

        transport = Transport.rest();
        assertEquals(Transport.Type.REST, transport.getType());
    }


    /**
     * Test {@link ValueType} option.
     */
    @Test
    public void testValueTypeOption()
    {
        ValueType valueType = ValueType.DEFAULT;

        assertEquals(String.class, valueType.getType());

        valueType = ValueType.of(String.class);
        assertEquals(String.class, valueType.getType());

        valueType = ValueType.of(Integer.class);
        assertEquals(Integer.class, valueType.getType());

        valueType = ValueType.of(Long.class);
        assertEquals(Long.class, valueType.getType());
    }
}
