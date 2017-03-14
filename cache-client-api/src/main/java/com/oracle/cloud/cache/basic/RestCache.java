/*
 * File: RestCache.java
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

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.oracle.cloud.cache.ServerCacheMetrics;
import com.oracle.cloud.cache.basic.io.Serializer;
import com.oracle.cloud.cache.basic.options.CacheOption;
import com.oracle.cloud.cache.basic.options.Expiry;
import com.oracle.cloud.cache.rest.MultiValue;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * An implementation of the {@link Cache} interface which uses the
 * REST API to issue requests to an Application Container Cloud Service (ACCS) Application Cache
 *
 * @param <V> value type for cache
 *
 * @author Aleksandar Seovic  2016.06.02
 */
public class RestCache<V> extends AbstractCache<V>
{
    /**
     * {@link SecurityException} to indicate HTTP code 403 was returned from server.
     */
    private static final SecurityException SECURITY_EXCEPTION =
        new SecurityException("Server responded with HTTP 403 (Forbidden)");

    /**
     * The {@link RestSession} to be used.
     */
    private final RestSession restSession;

    /**
     * The REST target for cache operations.
     */
    private final WebTarget cache;

    /**
     * The serializer for the cache.
     */
    private final Serializer serializer;


    /**
     * Constructs a {@link RestCache} with the given cache name, {@link RestSession} and
     * {@link CacheOption}s.
     *
     * @param cacheName  cache name
     * @param session    {@link RestSession} to use
     * @param options    {@link CacheOption}s to apply
     */
    @SuppressWarnings("unchecked")
    public RestCache(String         cacheName,
                     RestSession    session,
                     CacheOption... options)
    {
        super(cacheName, options);

        restSession = session;
        serializer  = getOptions().get(Serializer.class, session.getOptions().get(Serializer.class));
        this.cache  = session.getTarget().path(cacheName);
    }


    @Override
    protected V get(String key)
    {
        try
        {
            Response response = cache.path(key).request(APPLICATION_OCTET_STREAM).get();

            validateResponse(response.getStatus(), OK.getStatusCode(), NOT_FOUND.getStatusCode());

            return response.getStatus() == OK.getStatusCode()
                   ? serializer.deserialize(response.readEntity(byte[].class),
                                            getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V put(String  key,
                    V       value,
                    Expiry  expiry,
                    boolean returnOld)
    {
        try
        {
            Response response = cache.path(key)
                    .queryParam("ttl", expiry.getExpiry())
                    .queryParam("returnOld", returnOld)
                    .request(APPLICATION_OCTET_STREAM)
                    .put(Entity.entity(serializer.serialize(value), APPLICATION_OCTET_STREAM));

            validateResponse(response.getStatus(), OK.getStatusCode(), NO_CONTENT.getStatusCode());

            return response.getStatus() == OK.getStatusCode()
                   ? serializer.deserialize(response.readEntity(byte[].class),
                                            getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V putIfAbsent(String  key,
                            V       value,
                            Expiry  expiry,
                            boolean returnOld)
    {
        try
        {
            Response response = cache.path(key)
                    .queryParam("ttl", expiry.getExpiry())
                    .queryParam("returnOld", returnOld)
                    .request(APPLICATION_OCTET_STREAM)
                    .header("X-Method", "putIfAbsent")
                    .post(Entity.entity(serializer.serialize(value), APPLICATION_OCTET_STREAM));

            validateResponse(response.getStatus(), CONFLICT.getStatusCode(), NO_CONTENT.getStatusCode());

            return response.getStatus() == CONFLICT.getStatusCode()
                   ? serializer.deserialize(response.readEntity(byte[].class),
                                            getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V replace(String  key,
                        V       value,
                        Expiry  expiry,
                        boolean returnOld)
    {
        try
        {
            Response response = cache.path(key)
                    .queryParam("ttl", expiry.getExpiry())
                    .queryParam("returnOld", returnOld)
                    .request(APPLICATION_OCTET_STREAM)
                    .header("X-Method", "replace")
                    .post(Entity.entity(serializer.serialize(value), APPLICATION_OCTET_STREAM));

            validateResponse(response.getStatus(), NO_CONTENT.getStatusCode(), OK.getStatusCode());

            return response.getStatus() == OK.getStatusCode()
                   ? serializer.deserialize(response.readEntity(byte[].class),
                                            getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected boolean replaceValue(String key,
                                   V      valueOld,
                                   V      valueNew,
                                   Expiry expiry)
    {
        try
        {
            Response response = cache.path(key)
                    .queryParam("ttl", expiry.getExpiry())
                    .request(APPLICATION_OCTET_STREAM)
                    .header("X-Method", "replaceValue")
                    .post(Entity.entity(new MultiValue(serializer.serialize(valueOld),
                                                       serializer.serialize(valueNew)),
                                        "application/x-multivalue-octet-stream"));

            validateResponse(response.getStatus(), NO_CONTENT.getStatusCode(), CONFLICT.getStatusCode());

            // no content indicates successful replace
            return response.getStatus() == NO_CONTENT.getStatusCode();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected V remove(String  key,
                       boolean returnOld)
    {
        try
        {
            Response response = cache.path(key)
                    .queryParam("returnOld", returnOld)
                    .request()
                    .delete();

            validateResponse(response.getStatus(), OK.getStatusCode(), NO_CONTENT.getStatusCode());

            return response.getStatus() == OK.getStatusCode()
                   ? serializer.deserialize(response.readEntity(byte[].class),
                                            getValueClass()) : null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected boolean removeValue(String key,
                                  V      value)
    {
        try
        {
            Response response = cache.path(key)
                    .request(APPLICATION_OCTET_STREAM)
                    .header("X-Method", "removeValue")
                    .post(Entity.entity(serializer.serialize(value), APPLICATION_OCTET_STREAM));

            validateResponse(response.getStatus(), CONFLICT.getStatusCode(), NO_CONTENT.getStatusCode());

            // no content indicates successful remove
            return response.getStatus() == NO_CONTENT.getStatusCode();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected ServerCacheMetrics getServerMetrics()
    {
        Response response = cache.request(APPLICATION_JSON).get();

        validateResponse(response.getStatus(), OK.getStatusCode());

        return response.getStatus() == OK.getStatusCode() ? response.readEntity(ServerCacheMetrics.class) : null;
    }


    @Override
    public void clear()
    {
        Response response = cache.request().delete();

        validateResponse(response.getStatus(), NO_CONTENT.getStatusCode());
    }


    /**
     * Validates a given response code against a list of valid codes and return a
     * {@link SecurityException} if the http code is 403, or a {@link RuntimeException}
     * if the HTTP response is not in the given list. <br>
     *
     * @param responseCode    the response code returned for request
     * @param validResponses  and array of response codes that are considered valid
     *
     * @throws {@link SecurityException} if a HTTP 403 was returned
     * @throws {@link RuntimeException}  if any other response outside of the list
     *                                   was returned
     */
    private static void validateResponse(int    responseCode,
                                         int... validResponses)
    {
        if (responseCode == FORBIDDEN.getStatusCode())
        {
            throw SECURITY_EXCEPTION;
        }
        else
        {
            boolean found = false;

            for (int i = 0; i < validResponses.length; i++)
            {
                if (validResponses[i] == responseCode)
                {
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                throw new RuntimeException("Unexpected HTTP Response code " + responseCode
                                           + " was returned from the server.");
            }
        }
    }
}
