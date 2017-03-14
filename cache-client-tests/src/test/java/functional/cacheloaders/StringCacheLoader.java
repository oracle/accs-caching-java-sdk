/*
 * File: StringCacheLoader.java
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

package functional.cacheloaders;

import com.oracle.cloud.cache.basic.CacheLoader;
import functional.AbstractTest;

/**
 * A cache loader that will simulate a load when a key value is missing
 * and return a value as the key with a "-value" suffix.
 * <p>
 * Optionally you can setReturnNull to true and it will
 * cause the load operation to return null.
 * </p>
 * @author Tim Middleton  2016.06.10
 */
public class StringCacheLoader extends AbstractCacheLoader implements CacheLoader<String>
{
    /**
     * Suffix to append to value.
     */
    public static final String SUFFIX = "-value";


    @Override
    public String load(String key)
    {
        if (sleep)
        {
            // sleep a random number of millis
            AbstractTest.sleep(random.nextInt(100) * 10L);
        }

        loadCount++;

        return returnNull ? null : key + SUFFIX;
    }
}
 