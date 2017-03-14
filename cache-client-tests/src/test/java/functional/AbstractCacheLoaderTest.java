/*
 * File: AbstractCacheLoaderTest.java
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

import com.oracle.bedrock.deferred.Eventually;
import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.CacheLoader;
import com.oracle.cloud.cache.basic.LocalSession;
import com.oracle.cloud.cache.basic.Session;
import com.oracle.cloud.cache.basic.options.Expiry;
import com.oracle.cloud.cache.basic.options.ValueType;
import functional.cacheloaders.AbstractCacheLoader;
import functional.cacheloaders.PersonCacheLoader;
import functional.cacheloaders.StringCacheLoader;
import functional.model.Person;
import org.junit.Test;

import static com.oracle.bedrock.deferred.DeferredHelper.invoking;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * A set of tests that can be run using either Remote or Local session providers
 * to test CacheLoaders.
 *
 * @author Tim Middleton  2016.06.10
 */
public abstract class AbstractCacheLoaderTest extends AbstractTest
{
    /**
     * Ensures cache loader with String class works correctly.
     */
    @Test
    public void testStringCacheLoader()
    {
        Session             session     = getSession();

        CacheLoader<String> cacheLoader = new StringCacheLoader();

        Cache<String>       cache       = session.getCache("string-cache-loader-test", CacheLoader.of(cacheLoader));

        cache.clear();
        cache.resetMetrics();

        // test the cache loader is called on miss
        String value = cache.get("key1");

        assertEquals("key1" + StringCacheLoader.SUFFIX, value);

        assertEquals(1L, ((AbstractCacheLoader) cacheLoader).getLoadCount());

        // gets should be 1, loads should be 1 and misses = 1
        Eventually.assertThat(invoking(cache).getMetrics().getGetMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(1L));

        // ensure the cache loader is not called when key is present
        value = cache.get("key1");
        assertEquals("key1" + StringCacheLoader.SUFFIX, value);

        // loads should still be 1
        assertEquals(1L, ((StringCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getHitCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getGetMetrics().getCount(), is(2L));

        // test get with existing value in cache
        cache.put("key2", "value2");
        assertEquals("value2", cache.get("key2"));

        // loads should still be 1 as value existed
        assertEquals(1L, ((StringCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));

        // force cache loader to return null
        ((StringCacheLoader) cacheLoader).setReturnNull(true);
        value = cache.get("key3");
        assertNull(value);

        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(2L));
    }


    /**
     * Ensures cache loader with Person class works correctly.
     */
    @Test
    public void testPersonCacheLoader()
    {
        Session             session     = getSession();

        CacheLoader<Person> cacheLoader = new PersonCacheLoader();

        Cache<Person> cache = session.getCache("person-cache-loader-test",
                                               ValueType.of(Person.class),
                                               CacheLoader.of(cacheLoader));

        cache.clear();
        cache.resetMetrics();

        Person person = cache.get("p1");

        assertEquals(person, new Person("p1 Name"));

        assertEquals(1L, ((AbstractCacheLoader) cacheLoader).getLoadCount());

        Eventually.assertThat(invoking(cache).getMetrics().getGetMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(1L));

        // ensure cache loader is not called when value present
        person = cache.get("p1");
        assertEquals(1L, ((AbstractCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getHitCount(), is(1L));

        Person person2 = new Person("p2 Name");

        cache.put("p2", person2);
        assertEquals(person2, cache.get("p2"));

        // loads should still be 1 as value existed
        assertEquals(1L, ((AbstractCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getHitCount(), is(2L));

        // force cache loader to return null
        ((AbstractCacheLoader) cacheLoader).setReturnNull(true);

        Person p3 = cache.get("key3");

        assertNull(p3);

        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(2L));
    }


    /**
     * Test cache loader with larger number of calls.
     */
    @Test
    public void testLargeNumberOfCalls()
    {
        final long          MAX         = 1000L;

        Session             session     = getSession();

        CacheLoader<String> cacheLoader = new StringCacheLoader();

        // set sleep to false so we don't wait a long time
        ((AbstractCacheLoader) cacheLoader).setSleep(false);

        Cache<String> cache = session.getCache("large-cache-loader-test", CacheLoader.of(cacheLoader));

        cache.clear();
        cache.resetMetrics();

        getEntries(cache, "key-", (int) MAX, 0);

        // this should result in MAX values in the cache
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(MAX));

        // size only valid for non LocalSession
        if (!(session instanceof LocalSession))
        {
            Eventually.assertThat(invoking(cache).getMetrics().getSize(), is(greaterThan(0L)));
        }
    }


    /**
     * Ensures cache loader calls utilize expiry when set.
     */
    @Test
    public void testCacheLoaderWithExpiry()
    {
        Session             session     = getSession();

        CacheLoader<String> cacheLoader = new StringCacheLoader();
        Cache<String>       cache       = session.getCache("cache-loader-with-expiry", CacheLoader.of(cacheLoader));

        cache.clear();
        cache.resetMetrics();

        // issue a get which will cause a cache load with expiry
        String value = cache.get("key1", Expiry.of(Duration.ofSeconds(4)));

        assertEquals(value, "key1" + StringCacheLoader.SUFFIX);

        // ensure we registered a load
        assertEquals(1L, ((AbstractCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(1L));

        // sleep for 4.5 seconds and value should be gone which should cause another cache load
        sleep(4500L);
        value = cache.get("key1", Expiry.of(Duration.ofSeconds(4)));
        assertEquals(value, "key1" + StringCacheLoader.SUFFIX);

        // ensure we registered a second load due to expiry
        assertEquals(2L, ((AbstractCacheLoader) cacheLoader).getLoadCount());
        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(2L));
        Eventually.assertThat(invoking(cache).getMetrics().getMissCount(), is(2L));
    }


    /**
     * Ensures lambdas can be used as cache loaders.
     */
    @Test
    public void testLambdaCacheLoader()
    {
        Session session = getSession();

        Cache<Person> cache = session.getCache("lambda-cache-loader",
                                               ValueType.of(Person.class),
                                               CacheLoader.of((key) -> new Person(key + " Name")));

        cache.clear();
        cache.resetMetrics();

        Person p = cache.get("p1");

        assertNotNull(p);
        assertEquals("p1 Name", p.getName());

        Eventually.assertThat(invoking(cache).getMetrics().getLoadMetrics().getCount(), is(1L));
    }
}
 