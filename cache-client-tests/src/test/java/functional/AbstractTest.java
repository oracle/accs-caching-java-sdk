/*
 * File: AbstractTest.java
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
import com.oracle.cloud.cache.basic.Session;
import com.oracle.cloud.cache.basic.SessionProvider;

import static org.junit.Assert.assertEquals;

/**
 * Abstract class with common test functions.
 *
 * @author Tim Middleton 2016.09.09
 */
public class AbstractTest
{
    private static final char[] randomData = new char[] {'A', 'a', '$', '^', '6', 'h', 'j', 'Z', 'g', 'H'};

    /**
     * The session provider.
     */
    private static SessionProvider sessionProvider;

    /**
     * The Session to use.
     */
    private static Session session;


    /**
     * Returns the {@link SessionProvider} for this test.
     *
     * @return  the {@link SessionProvider} for this test
     */
    protected static SessionProvider getSessionProvider()
    {
        return sessionProvider;
    }


    /**
     * Sets the {@link SessionProvider} for this test.
     *
     * @param provider  the {@link SessionProvider} for this test
     */
    protected static void setSessionProvider(SessionProvider provider)
    {
        sessionProvider = provider;
    }


    /**
     * Returns the {@link Session} for this test.
     *
     * @return  the {@link Session} for this test
     */
    protected static Session getSession()
    {
        return session;
    }


    /**
     * Sets the {@link Session} for this test.
     * @param sess  the {@link Session} for this test
     */
    protected static void setSession(Session sess)
    {
        session = sess;
    }


    /**
     * Add data and assert that the entry exists.
     *
     * @param cache  cache to insert into
     * @param key    key to use
     * @param value  value to insert
     */
    protected void addAndAssert(Cache  cache,
                                String key,
                                Object value)
    {
        cache.put(key, value);
        assertEquals(value, cache.get(key));
    }


    /**
     * Sleep for a duration of millis.
     *
     * @param millis millis to sleep for.
     */
    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Returns a "randomish" String for nDataSize size.
     *
     * @param dataSize  the size to make the String
     *
     * @return the new String
     */
    protected String getRandomValue(int dataSize)
    {
        char[] aChars = new char[dataSize];
        int    nLen   = randomData.length;

        for (int i = 0; i < dataSize; i++)
        {
            aChars[i] = randomData[i % nLen];
        }

        return new String(aChars);
    }


    /**
     * Adds a specified number of entries with the key and values being the supplied prefixes
     * with the entry number appended.
     *
     * @param cache        cache to add to
     * @param keyPrefix    the prefix for the key
     * @param valuePrefix  the prefix for the value
     * @param entries      number of entries
     * @param offset       offset to start from
     */
    protected void putEntries(Cache  cache,
                              String keyPrefix,
                              String valuePrefix,
                              int    entries,
                              int    offset)
    {
        System.out.print("Putting " + entries + " entries starting at " + offset + "...");

        for (int i = offset; i < offset + entries; i++)
        {
            cache.put(keyPrefix + i, valuePrefix + i);
        }
    }


    /**
     * Gets a specified number of entries with the key and values being the supplied prefixes
     * with the entry number appended.
     *
     * @param cache      cache to get from
     * @param keyPrefix  the prefix for the key
     * @param entries    number of entries
     * @param offset     offset to start from
     */
    protected void getEntries(Cache  cache,
                              String keyPrefix,
                              int    entries,
                              int    offset)
    {
        System.out.print("Getting " + entries + " entries starting at " + offset + "...");

        for (int i = offset; i < offset + entries; i++)
        {
            String sValue = (String) cache.get(keyPrefix + i);
        }
    }


    /**
     * Remove a specified number of entries with the key and values being the supplied prefixes
     * with the entry number appended.
     *
     * @param cache      cache to get from
     * @param keyPrefix  the prefix for the key
     * @param entries    number of entries
     * @param offset     offset to start from
     */
    protected void removeEntries(Cache  cache,
                                 String keyPrefix,
                                 int    entries,
                                 int    offset)
    {
        System.out.print("Removing " + entries + " entries starting at " + offset + "...");

        for (int i = offset; i < offset + entries; i++)
        {
            cache.remove(keyPrefix + i);
        }
    }
}
