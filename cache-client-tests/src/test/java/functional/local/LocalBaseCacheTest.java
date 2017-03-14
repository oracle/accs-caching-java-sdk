/*
 * File: LocalBaseCacheTest.java
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

package functional.local;

import com.oracle.cloud.cache.basic.LocalSession;
import functional.AbstractBaseCacheTest;
import org.junit.Before;

/**
 * Base cache tests using {@link com.oracle.cloud.cache.basic.LocalSessionProvider}.
 *
 * @author Tim Middleton  2016.06.09
 */
public class LocalBaseCacheTest extends AbstractBaseCacheTest
{
    /**
     * Initializes the {@link com.oracle.cloud.cache.basic.Session}.
     */
    @Before
    public void initialize()
    {
        setSession(new LocalSession());
    }
}
 