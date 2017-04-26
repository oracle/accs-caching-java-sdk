/*
 * File: RunExampleClient.java
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

package com.oracle.cloud.cache.examples;

/**
 * Example that uses Java cache-client-api to access
 * Application Container Cloud Service (ACCS) Application Cache.
 *
 * @author Tim Middleton 2017.01.17
 */
public class RunExampleClient
{
    /**
     * Entry point to run {@link ExampleCacheClient}.
     *
     * @param args  arguments to main
     */
    public static void main(String[] args)
    {
        // environment variable for caching url
        String cacheHost = System.getenv("CACHING_INTERNAL_CACHE_URL");
        String gprcUrl   = cacheHost;
        String restUrl   = cacheHost + "/ccs";

        ExampleCacheClient.header("Starting Cache Service Java Example");
        ExampleCacheClient.log("GRPC URL=" + gprcUrl + ", REST URL=" + restUrl);

        // run using LocalSession
        new ExampleCacheClient().runExampleMain();

        // Uncomment the following two examples and set the URL's if deploying in ACCS
        // run using Java via grpc transport
        // new ExampleCacheClient(gprcUrl, Transport.grpc()).runExampleMain();

        // run using Java via rest transport
        // new ExampleCacheClient(restUrl, Transport.rest()).runExampleMain();
    }
}
 