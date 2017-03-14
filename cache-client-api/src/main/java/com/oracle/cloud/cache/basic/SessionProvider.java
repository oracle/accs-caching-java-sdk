/*
 * File: SessionProvider.java
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

package com.oracle.cloud.cache.basic;

import com.oracle.cloud.cache.basic.options.SessionOption;

/**
 * Allows users to establish a {@link Session} to an Application Container Cloud Service
 * (ACCS) Application Cache.
 *
 * @author Aleksandar Seovic  2016.05.20
 * @see LocalSessionProvider
 * @see RemoteSessionProvider
 */
public interface SessionProvider
{
    /**
     * Creates a new {@link Session} with the specified {@link SessionOption}.
     *
     * @param options the cache session options
     * @return the Session
     */
    Session createSession(SessionOption... options);
}
