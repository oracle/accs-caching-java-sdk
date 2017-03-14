/*
 * File: LocalSessionProvider.java
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
 * An implementation of a {@link SessionProvider}, which provides a {@link LocalSession}
 * in the same JVM as the process that instantiates a LocalSessionProvider object.
 *
 * @author Aleksandar Seovic  2016.06.02
 */
public class LocalSessionProvider implements SessionProvider
{
    @Override
    public Session createSession(SessionOption... options)
    {
        return new LocalSession();
    }
}
