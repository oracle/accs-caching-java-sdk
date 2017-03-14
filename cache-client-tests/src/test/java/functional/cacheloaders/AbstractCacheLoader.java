/*
 * File: AbstractCacheLoader.java
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

import java.util.Random;

/**
 * Abstract class containing common functionality for cache loaders.
 *
 * @author Tim Middleton 2016.06.10
 */
public abstract class AbstractCacheLoader
{
    /**
     * Number of times the loader has been called
     */
    protected long loadCount = 0;

    /**
     * Indicates if the cache loader should return null.
     */
    protected boolean returnNull = false;

    /**
     * Indicates if sleep should be executed.
     */
    protected boolean sleep = true;

    /**
     * Random number generator for sleep.
     */
    protected final Random random = new Random();


    /**
     * Returns the number of times the loader has been called.
     *
     * @return the number of times the loader has been called
     */
    public long getLoadCount()
    {
        return loadCount;
    }


    /**
     * Sets an indicator to tell the loader to return null.
     *
     * @param returnNull if true will return null
     */
    public void setReturnNull(boolean returnNull)
    {
        this.returnNull = returnNull;
    }


    /**
     * Indicates if the {@link com.oracle.cloud.cache.basic.CacheLoader} should sleep.
     *
     * @param sleep
     */
    public void setSleep(boolean sleep)
    {
        this.sleep = sleep;
    }
}
