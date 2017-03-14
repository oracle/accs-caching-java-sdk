/*
 * File: AbstractBaseCacheTest.java
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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.options.Expiry;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.basic.options.ValueType;
import functional.model.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * A set of tests that can be run using either Remote or Local session providers.
 *
 * @author Tim Middleton  2016.06.09
 */
public abstract class AbstractBaseCacheTest extends AbstractTest
{
    /**
     * Ensures that clear() function works.
     */
    @Test
    public void testClear()
    {
        Cache<String> cache = getSession().getCache("clear-cache");

        addAndAssert(cache, "one", "one");
        addAndAssert(cache, "two", "two");
        cache.clear();

        assertNull(cache.get("one"));
        assertNull(cache.get("two"));
    }


    /**
     * Ensures that we can get and put Strings.
     */
    @Test
    public void testGetPutStrings()
    {
        Cache<String> cache = getSession().getCache("strings");

        cache.clear();

        addAndAssert(cache, "aleks", "Aleksandar Seovic");
        addAndAssert(cache, "tim", "Tim Middleton");

        assertEquals("Aleksandar Seovic", cache.get("aleks"));
        assertEquals("Tim Middleton", cache.get("tim"));

        // test various expiry values
        cache.put("brian", "Brian Oliver", Expiry.of(100, TimeUnit.MILLISECONDS));
        sleep(120L);
        assertNull("Cache entry is still present but should have expired", cache.get("brian"));

        cache.put("brian", "Brian Oliver", Expiry.of(Duration.ofMillis(2500L)));
        sleep(2510L);
        assertNull("Cache entry is still present but should have expired", cache.get("brian"));

        // test return values
        cache.put("brian", "Brian Oliver");
        assertEquals("Brian Oliver", cache.get("brian"));

        String oldValue = cache.put("brian", "BRIAN OLIVER", Return.oldValue());

        assertNotNull(oldValue);
        assertEquals(oldValue, "Brian Oliver");

        System.out.println(cache.getMetrics());
    }


    /**
     * Ensures that we can get and put Integers.
     */
    @Test
    public void testGetPutInts()
    {
        Cache<Integer> cache = getSession().getCache("numbers", ValueType.of(Integer.class));

        cache.clear();

        addAndAssert(cache, "one", 1);
        addAndAssert(cache, "two", 2);
    }


    /**
     * Ensures we can get and put Objects.
     */
    @Test
    public void testGetPutObjects()
    {
        Cache<Person> cache = getSession().getCache("people", ValueType.of(Person.class));

        cache.clear();

        cache.put("aleks", new Person("Aleks"));
        cache.put("tim", new Person("Tim"));

        assertEquals("Aleks", cache.get("aleks").getName());
        assertEquals("Tim", cache.get("tim").getName());
    }


    /**
     * Ensures putIfAbsent works correctly.
     */
    @Test
    public void testGetPutIfAbsentStrings()
    {
        Cache<String> cache = getSession().getCache("put-if-absent");

        cache.clear();

        assertNull(cache.get("aleks"));
        cache.putIfAbsent("aleks", "Aleksandar Seovic");

        // should have succeeded as value already exists
        assertEquals("Aleksandar Seovic", cache.get("aleks"));

        cache.put("tim", "Tim Middleton");
        cache.putIfAbsent("tim", "Timbo");

        // should not have modified as value is not absent
        assertEquals("Tim Middleton", cache.get("tim"));

        // test various expiry values
        cache.putIfAbsent("brian", "Brian Oliver", Expiry.of(100, TimeUnit.MILLISECONDS));
        sleep(120L);
        assertNull("Cache entry is still present but should have expired", cache.get("brian"));

        cache.putIfAbsent("brian", "Brian Oliver", Expiry.of(Duration.ofMillis(2500L)));
        sleep(2510L);
        assertNull("Cache entry is still present but should have expired", cache.get("brian"));

        // test return values
        String oldValue = cache.putIfAbsent("brian", "Brian Oliver", Return.oldValue());

        assertNull(oldValue);

        // test that if we do pitIfAbsent and value is already there we shoudl get return value
        oldValue = cache.putIfAbsent("brian", "BRIAN", Return.oldValue());
        assertEquals("Brian Oliver", oldValue);

        // value shoudl not have changed
        assertEquals(cache.get("brian"), "Brian Oliver");

        // test expiry and return value
        cache.remove("brian");
        assertNull(cache.get("brian"));

        oldValue = cache.putIfAbsent("brian", "Brian Oliver", Expiry.of(100, TimeUnit.MILLISECONDS), Return.oldValue());
        assertNull(oldValue);
    }


    /**
     * Ensures remove works correctly.
     */
    @Test
    public void testRemove()
    {
        Cache<String> cache = getSession().getCache("remove-test");

        cache.clear();
        addAndAssert(cache, "tim", "Tim Middleton");

        cache.remove("tim");
        assertNull(cache.get("tim"));

        // test remove returning old value
        cache.put("tim", "Tim Middleton");

        String result = cache.remove("tim", Return.oldValue());

        assertNotNull(result);
        assertEquals(result, "Tim Middleton");

        // test remove returning no value
        cache.put("tim", "Tim Middleton");
        result = cache.remove("tim", Return.nothing());
        assertNull(result);
    }


    /**
     * Ensures removeValue works correctly.
     */
    @Test
    public void testRemoveValue()
    {
        Cache<String> cache = getSession().getCache("remove-value");

        cache.clear();
        addAndAssert(cache, "tim", "Tim Middleton");

        // should not remove as value does not equal
        boolean fRemoved = cache.remove("tim", "Timbo");

        assertEquals(fRemoved, false);
        assertEquals(cache.get("tim"), "Tim Middleton");

        // should remove as value does equal
        fRemoved = cache.remove("tim", "Tim Middleton");
        assertEquals(fRemoved, true);
        assertNull(cache.get("tim"));

        Cache<Person> personCache = getSession().getCache("people", ValueType.of(Person.class));

        personCache.clear();

        Person person1 = new Person("Tim");

        personCache.put("p1", person1);
        assertEquals(person1, personCache.get("p1"));

        Person person2 = new Person("Aleks");

        personCache.put("p2", person2);
        assertEquals(person2, personCache.get("p2"));

        // should not remove as value doesn't equal
        fRemoved = personCache.remove("p1", person2);
        assertEquals(fRemoved, false);
        assertNotNull(personCache.get("p1"));

        // shoudl remove as value matches
        fRemoved = personCache.remove("p1", person1);
        assertEquals(fRemoved, true);
        assertNull(personCache.get("p1"));
    }


    /**
     * Ensures replace works correctly.
     */
    @Test
    public void testReplace()
    {
        Cache<String> cache = getSession().getCache("remove-value");

        cache.clear();
        addAndAssert(cache, "tim", "Tim Middleton");

        // should succeed as currently matched to value
        cache.replace("tim", "Timbo");
        assertEquals("Timbo", cache.get("tim"));

        // should not succeed as no match
        cache.replace("aleks", "Aleksandar Seovic");
        assertNull(cache.get("aleks"));

        addAndAssert(cache, "aleks", "Aleksandar Seovic");

        // should succeed as currently matched to value and should return value
        String oldValue = cache.replace("aleks", "Aleks", Return.oldValue());

        assertEquals("Aleksandar Seovic", oldValue);
        assertEquals("Aleks", cache.get("aleks"));

        // replace and use TTL of 10 seconds
        oldValue = cache.replace("aleks", "ALEKS", Return.oldValue(), Expiry.of(6, TimeUnit.SECONDS));

        // old value should be set
        assertEquals("Aleks", oldValue);

        // ne value should be set
        assertEquals("ALEKS", cache.get("aleks"));

        // now wait 6.1 seconds and the entry should be expired
        sleep(6100L);
        assertNull(cache.get("aleks"));
    }


    /**
     * Ensures replaceValue works correctly.
     */
    @Test
    public void testReplaceValue()
    {
        Cache<String> cache = getSession().getCache("replace-value");

        cache.clear();
        addAndAssert(cache, "tim", "Tim Middleton");

        // should work as old value matches
        boolean fReplaced = cache.replace("tim", "Tim Middleton", "New Tim Middleton");

        assertEquals(true, fReplaced);
        assertEquals("New Tim Middleton", cache.get("tim"));

        // should not work as old value doesn't match
        fReplaced = cache.replace("tim", "Old Value Not Matching", "New Value");
        assertEquals(false, fReplaced);

        // should work but them will be expired.
        fReplaced = cache.replace("tim",
                                  "New Tim Middleton",
                                  "Newer Tim Middleton",
                                  Expiry.of(Duration.ofMillis(5000L)));
        assertEquals(true, fReplaced);
        assertEquals("Newer Tim Middleton", cache.get("tim"));

        sleep(5010L);

        // should be gone now
        assertNull(cache.get("tim"));
    }
}
