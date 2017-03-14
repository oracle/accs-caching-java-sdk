/*
 * File: MultiValueTest.java
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

package com.oracle.cloud.cache.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for MultiValue.
 *
 * @author Tim Middleton  2016.06.28
 */
public class MultiValueTest
{
    /**                    
     * Test the {@link MultiValue} class.
     *
     * @throws IOException when any I/O related errors
     */
    @Test
    public void testMultiValue() throws IOException
    {
        String     sEntry1    = "Entry 1";
        String     sEntry2    = "Entry two";

        MultiValue multiValue = new MultiValue(sEntry1.getBytes(), sEntry2.getBytes());

        byte[]     aBytes     = multiValue.toByteArray();

        int        i          = 0;

        // count of number of entries
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(2, aBytes[i++]);

        // first entry length
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(sEntry1.length(), aBytes[i++]);

        // first entry
        byte[] aEntry1Bytes = sEntry1.getBytes();

        for (int j = 0; j < aEntry1Bytes.length; j++)
        {
            assertEquals(aEntry1Bytes[j], aBytes[i++]);
        }

        // second entry length
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(0, aBytes[i++]);
        assertEquals(sEntry2.length(), aBytes[i++]);

        // second entry
        byte[] aEntry2Bytes = sEntry2.getBytes();

        for (int j = 0; j < aEntry2Bytes.length; j++)
        {
            assertEquals(aEntry2Bytes[j], aBytes[i++]);
        }

    }


    /**
     * Test reading a {@link MultiValue} from a binary file.
     *
     * @throws IOException          if any I/O related errors
     * @throws URISyntaxException   if and issues reading the file
     */
    @Test
    public void testReadFromFile() throws URISyntaxException, IOException
    {
        // read a multi value from file
        URL        url        = this.getClass().getClassLoader().getResource("sample.bin");

        byte[]     abBytes    = Files.readAllBytes(Paths.get(url.toURI()));

        MultiValue multiValue = MultiValue.from(abBytes);

        assertEquals("{\"customerId\": \"key3\", \"name\": \"Tim Middleton - VERSION 2\"}",
                     new String(multiValue.get(0)));
        assertEquals("{\"customerId\": \"key3\", \"name\": \"Tim Middleton - VERSION 3\"}",
                     new String(multiValue.get(1)));
    }
}
