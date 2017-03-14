/*
 * File: AbstractLargeDataCacheTest.java
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

package functional;

import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.basic.options.ValueType;
import functional.model.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Various tests for larger volumes and size of data for {@link Cache} API.
 *
 * @author Tim Middleton  2016.05.24
 */
public abstract class AbstractLargeDataCacheTest extends AbstractTest
{
    /**
     * Number of entries to insert for large tests.
     */
    private static final int COUNT = 1000;

    /**
     * Number of entries for large value tests.
     */
    private static final int LARGE_COUNT = 500;


    /**
     * Ensures large number of String entries can be safely put/get/removed.
     */
    @Test
    public void testLargeNumberOfStringEntries()
    {
        Cache<String> cache = getSession().getCache("large-number-of-strings");

        cache.clear();

        System.out.println("Insert " + COUNT + " entries");

        // insert large number key/values
        for (int i = 0; i < COUNT; i++)
        {
            cache.put("key-" + i, "value-" + +i);
        }

        System.out.println("Assert " + COUNT + " entries");

        // assert they are all there
        for (int i = 0; i < COUNT; i++)
        {
            String value = cache.get("key-" + i);

            assertNotNull(value);
            assertEquals("value-" + i, value);
        }

        System.out.println("Remove " + COUNT + " entries");

        // remove each one only if they exist
        for (int i = 0; i < COUNT; i++)
        {
            boolean removed = cache.remove("key-" + i, "value-" + i);

            assertEquals(true, removed);
            assertNull(cache.get("key-" + i));
        }
    }


    /**
     * Ensures large number of Object entries can be safely put/get/removed.
     */
    @Test
    public void testLargeNumberOfObjects()
    {
        Cache<Person> cache = getSession().getCache("large-number-of-people", ValueType.of(Person.class));

        cache.clear();

        System.out.println("Insert " + COUNT + " Person entries");

        // insert large number key/values
        for (int i = 0; i < COUNT; i++)
        {
            cache.put("person-" + i, new Person("name-" + i));
        }

        System.out.println("Assert " + COUNT + " Person entries");

        // assert they are all there
        for (int i = 0; i < COUNT; i++)
        {
            Person person = cache.get("person-" + i);

            assertNotNull(person);
            assertEquals(new Person("name-" + i), person);
        }

        System.out.println("Remove " + COUNT + " Person entries");

        // remove each one only if they exist
        for (int i = 0; i < COUNT; i++)
        {
            boolean removed = cache.remove("person-" + i, new Person("name-" + i));

            assertEquals(true, removed);
            assertNull(cache.get("person-" + i));
        }
    }


    /**
     * Ensures large String entries can be safely put/get/removed.
     */
    @Test
    public void testLargeStringEntries()
    {
        Cache<String> cache = getSession().getCache("large-strings");

        cache.clear();

        // use to mod get predicable sizes
        int modValue = 256;
        int baseSize = 20480;

        System.out.println("Insert " + LARGE_COUNT + " entries");

        for (int i = 0; i < LARGE_COUNT; i++)
        {
            cache.put("key-" + i, getRandomValue((i % modValue) + baseSize));
        }

        System.out.println("Assert " + LARGE_COUNT + " entries");

        for (int i = 0; i < LARGE_COUNT; i++)
        {
            String sValue = cache.get("key-" + i);

            assertEquals(getRandomValue((i % modValue) + baseSize), sValue);
        }

        System.out.println("Remove " + LARGE_COUNT + " entries");

        for (int i = 0; i < LARGE_COUNT; i++)
        {
            String sOldValue = cache.remove("key-" + i, Return.oldValue());

            assertEquals(getRandomValue((i % modValue) + baseSize), sOldValue);
            assertNull(cache.get("key-" + i));
        }
    }
}
     