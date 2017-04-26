/*
 * File: ExampleCacheClient.java
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

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.CacheLoader;
import com.oracle.cloud.cache.basic.LocalSessionProvider;
import com.oracle.cloud.cache.basic.RemoteSessionProvider;
import com.oracle.cloud.cache.basic.Session;
import com.oracle.cloud.cache.basic.options.Expiry;
import com.oracle.cloud.cache.basic.options.Return;
import com.oracle.cloud.cache.basic.options.SessionOption;
import com.oracle.cloud.cache.basic.options.Transport;
import com.oracle.cloud.cache.basic.options.ValueType;
import com.oracle.cloud.cache.metrics.CacheMetrics;
import com.oracle.cloud.cache.metrics.TimerSnapshot;

/**
 * Basic Java API examples for cache-client-api for accessing an 
 * Application Container Cloud Service (ACCS) Application Cache.
 *
 * @author Tim Middleton 2016.06.01
 */
public class ExampleCacheClient
{
    /**
     * Random data for value generation.
     */
    private static final char[] RANDOM_DATA = new char[] {'A', 'a', '$', '^', '6', 'h', 'j', 'Z', 'g', 'H', 'X', '^',
                                                          'H', 'H', '1', '2', '#', '$', '%', '^', 'g'};

    /**
     * The {@link Session} to access the cache.
     */
    private final Session session;


    /**
     * Constructs an ExampleCloudCacheClient using the {@link LocalSessionProvider}.
     */
    public ExampleCacheClient()
    {
        session = new LocalSessionProvider().createSession();
    }


    /**
     * Constructs an ExampleCloudCacheClient using the url and transport.
     *
     * @param url     url to connect to
     * @param option  options to apply to create session e.g.
     *                either {@link Transport}.grpc() or {@link Transport}.rest()
     */
    public ExampleCacheClient(String        url,
                              SessionOption option)
    {
        session = new RemoteSessionProvider(url).createSession(option);
    }


    /**
     * Entry point to run examples.
     */
    public void runExampleMain()
    {
        header("");
        header("Session is " + session);
        header("");

        try
        {
            runExampleBasicPutGet();

            runReplaceExample();

            runRemoveExample();

            runPOJOExample();

            runIntegerExample();

            runCacheLoaderExample();

            runLambdaCacheLoaderExample();

            runLargeNumberOfOperations();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Examples using basic put and get operation including expiry.
     */
    private void runExampleBasicPutGet()
    {
        header("Running Basic Put/Get Example");

        Cache<String> cache = getSession().getCache("basic-put-get-examples");

        cache.clear();
        cache.resetMetrics();

        // Basic Put example
        log("Basic put");
        cache.put("tim", "Tim Middleton");
        valueIs("tim", cache);
        logIndent("Size of cache from metrics is " + cache.getMetrics().getSize());

        log("Put example returning old value");

        String sOldValue = cache.put("tim", "TIM MIDDLETON", Return.oldValue());

        logIndent("Old value returned is " + sOldValue);
        valueIs("tim", cache);

        log("PutIfAbsent when not absent");
        cache.putIfAbsent("tim", "This Won't Work");
        valueIs("tim", cache);

        log("PutIfAbsent when absent");
        cache.putIfAbsent("paul", "Paul Mackin");
        valueIs("paul", cache);

        // put with expiry
        long cExpiry = 2000L;

        log("Putting value with expiry of " + cExpiry + " ms");
        cache.put("luk", "luk Ho", Expiry.of(Duration.ofMillis(cExpiry)));
        valueIs("luk", cache);
        sleep("Sleeping " + (cExpiry + 100L), cExpiry + 100L);
        valueIs("luk", cache);

        dumpMetrics(cache);
    }


    /**
     * Examples using replace operation.
     */
    private void runReplaceExample()
    {
        header("Running Replace Example");

        Cache<String> cache = getSession().getCache("replace-examples");

        cache.clear();
        cache.resetMetrics();

        log("Add entry");
        cache.put("key1", "value1");
        valueIs("key1", cache);

        log("Replace if key mapped to some value");
        cache.replace("key1", "new value1");
        valueIs("key1", cache);

        log("Replace if key mapped to a particular value of \"old value\"");

        boolean replaced = cache.replace("key1", "old value", "newer value");

        valueIs("key1", cache);
        logIndent("Replaced is " + replaced);

        log("Replace if key mapped to a particular value of \"new value1\"");
        replaced = cache.replace("key1", "new value1", "much newer value");
        valueIs("key1", cache);
        logIndent("Replaced is " + replaced);

        log("Running replace with expiry of 2 seconds");

        String oldValue = cache.replace("key1", "even newer value", Expiry.of(2, TimeUnit.SECONDS));

        logIndent("Old value is " + oldValue);
        valueIs("key1", cache);
        sleep("Sleep 3 seconds", 3000L);
        valueIs("key1", cache);

        dumpMetrics(cache);
    }


    /**
     * Examples using remove operations.
     */
    private void runRemoveExample()
    {
        header("Running Delete Example");

        Cache<String> cache = getSession().getCache("delete-examples");

        cache.clear();
        cache.resetMetrics();

        log("Add entry");
        cache.put("car1", "Holden");
        cache.put("car2", "Ford");
        valueIs("car1", cache);
        valueIs("car2", cache);

        log("Remove entry");
        cache.remove("car1");
        valueIs("car1", cache);

        log("Remove entry returning old value");

        String oldValue = cache.remove("car2", Return.oldValue());

        logIndent("Old value is " + oldValue);
        valueIs("car2", cache);

        log("Add entry");
        cache.put("car3", "Audi");
        cache.put("car4", "BMW");
        valueIs("car3", cache);
        valueIs("car4", cache);

        log("Remove only if value matches - when it doesn't");

        boolean removed = cache.remove("car3", "Mustang");

        valueIs("car3", cache);
        logIndent("Removed is " + removed);

        log("Remove only if value matches - when it does");
        removed = cache.remove("car4", "BMW", Return.oldValue());
        valueIs("car4", cache);
        logIndent("Removed is " + removed);

        dumpMetrics(cache);
    }


    /**
     * Examples using POJO's as value.
     */
    private void runPOJOExample()
    {
        header("Running POJO Example");

        Cache<Customer> cache = getSession().getCache("customers-example", ValueType.of(Customer.class));

        cache.clear();
        cache.resetMetrics();

        Customer customer1 = new Customer("c1", "Tom Jones");

        log("Adding Customer " + customer1);
        cache.put(customer1.getId(), customer1);
        valueIs(customer1.getId(), cache);

        log("Updating customer balance by $100.00 but only if it has not been changed from original");

        Customer c = cache.get("c1");

        c.adjustBalance(100);
        cache.replace(customer1.getId(), customer1, c);
        valueIs(customer1.getId(), cache);

        Customer customer2 = new Customer("c2", "John Williams");

        log("Adding Customer " + customer2 + " using putIfAbsent");
        cache.putIfAbsent(customer2.getId(), customer2);
        valueIs(customer2.getId(), cache);

        customer2.adjustBalance(200);
        customer2.setName(customer2.getName().toUpperCase());
        log("Update customer and return old value");

        Customer oldCustomer = cache.replace(customer2.getId(), customer2, Return.oldValue());

        logIndent("Old customer is " + oldCustomer);
        valueIs(customer2.getId(), cache);

        Customer customer3 = new Customer("c3", "Maynard Ferguson");

        log("Adding Customer " + customer3 + " with expiry 5 seconds");
        cache.put(customer3.getId(), customer3, Expiry.of(Duration.ofSeconds(5)));
        valueIs(customer3.getId(), cache);
        sleep("Sleeping 5.5 seconds", 5500L);
        valueIs(customer3.getId(), cache);

        dumpMetrics(cache);
    }


    /**
     * Examples using Integer as value.
     */
    private void runIntegerExample()
    {
        header("Running Integer Example");

        Cache<Integer> cache = getSession().getCache("integer-example", ValueType.of(Integer.class));

        cache.clear();
        cache.resetMetrics();

        log("Adding new Integer");
        cache.put("1", 1);
        valueIs("1", cache);

        log("Replace the value if it has not changed");
        cache.replace("1", 1, 2);
        valueIs("1", cache);

        log("Remove the value if matches");

        boolean fRemoved = cache.remove("1", 2);

        valueIs("1", cache);
        logIndent("Removed is " + fRemoved);

        dumpMetrics(cache);
    }


    /**
     * Example to work with CacheLoader
     */
    private void runCacheLoaderExample()
    {
        header("Running CacheLoader example");

        CustomerCacheLoader cacheLoader = new CustomerCacheLoader(" name");
        Session             session     = getSession();

        // get cache with type and cache loader
        Cache<Customer> cache = session.getCache("customer-cache-loader",
                                                 ValueType.of(Customer.class),
                                                 CacheLoader.of(cacheLoader));

        cache.clear();
        cache.resetMetrics();

        log("Issuing cache get with no entry to cause load");

        Customer customer = cache.get("customer1");

        logIndent("Customer returned via load is " + customer);
        logIndent("Number of loads is " + cacheLoader.getLoads());

        log("Issue cache get again with same key showing no load");
        customer = cache.get("customer1");
        logIndent("Customer returned via load is " + customer);
        logIndent("Number of loads is " + cacheLoader.getLoads());

        log("Issue cache get with expiry of 5 sec for entries loaded");

        Customer customer2 = cache.get("customer2", Expiry.of(Duration.ofMillis(5000L)));

        logIndent("Customer returned via load is " + customer2);
        logIndent("Number of loads is " + cacheLoader.getLoads());

        logIndent("Sleeping 5.1 sec");
        sleep(5100L);
        logIndent("Changing cache loader suffix");
        cacheLoader.setSuffix(" new suffix");
        logIndent("Issue get for customer, should load as expired");
        customer2 = cache.get("customer2");
        logIndent("Customer returned via load is " + customer2);
        logIndent("Number of loads is " + cacheLoader.getLoads());

        dumpMetrics(cache);
    }


    /**
     * Example to show lambda being used as cache loader.
     */
    private void runLambdaCacheLoaderExample()
    {
        header("Running Lambda CacheLoader example");

        Session session = getSession();

        logIndent("Creating customer cache with lambda cache loader");

        // get cache with type and cache loader
        Cache<Customer> cache = session.getCache("customer-lambda-cache-loader",
                                                 ValueType.of(Customer.class),
                                                 CacheLoader.of(
                                                     key -> {
                                                         Customer c = new Customer(key, "Customer " + key);

                                                         c.adjustBalance(+1000.0d);

                                                         return c;
                                                     }));

        cache.clear();
        cache.resetMetrics();

        Customer customer = cache.get("c1");

        logIndent("Customer returned from get is " + customer);
        dumpMetrics(cache);
    }


    /**
     * Run a large number of operations to get some more realistic metrics.
     */
    private void runLargeNumberOfOperations() throws InterruptedException
    {
        final int       MAX            = 1000;
        final int       KB             = 1024;
        final int       THREADS        = 10;

        ExecutorService executor       = Executors.newFixedThreadPool(THREADS);
        CountDownLatch  countDownLatch = new CountDownLatch(THREADS);

        for (int t = 0; t < THREADS; t++)
        {
            executor.submit(
                () -> {
                    System.out.println("Start thread " + Thread.currentThread());

                    Cache<String> cache = getSession().getCache("large-examples");

                    cache.clear();
                    cache.resetMetrics();

                    for (int i = 0; i < 5; i++)
                    {
                        exerciseCache(cache, KB, MAX, true);
                    }

                    System.out.println("Metrics from " + Thread.currentThread() + "\n" + cache.getMetrics().toString());
                    countDownLatch.countDown();
                });
        }

        countDownLatch.await();
        executor.shutdown();
    }


    /**
     * Exercises the given cache by issuing multiple operations.
     *
     * @param cache     cache to run against
     * @param dataSize  the size of the data
     * @param max       the maximum number of iterations
     * @param log       if true, messages will be logged
     */
    private void exerciseCache(Cache<String> cache,
                         int           dataSize,
                         int           max,
                         boolean       log)
    {
        String sValue = getRandomValue(dataSize);

        if (log)
        {
            log("Working on " + max + " entries of size " + dataSize);
            logIndent("Put " + max);
        }

        for (int i = 0; i < max; i++)
        {
            cache.put("key-" + i, sValue + i);
        }

        if (log)
        {
            logIndent("Get " + max);
        }

        for (int i = 0; i < max; i++)
        {
            cache.get("key-" + i);
        }

        if (log)
        {
            logIndent("Remove " + max);
        }

        for (int i = 0; i < max; i++)
        {
            cache.remove("key-" + i);
        }

        if (log)
        {
            logIndent("Gets to force " + (max / 5) + " misses");
        }

        for (int i = 0; i < max / 5; i++)
        {
            cache.get("key-" + i);
        }

        // display metrics
        CacheMetrics metrics = cache.getMetrics();

        StringBuilder sb = new StringBuilder("\nMetrics:\n")
                .append("Entries:   ")
                .append(String.format("%,d", metrics.getCount()))
                .append('\n')
                .append("Size:      ")
                .append(String.format("%,d", metrics.getSize()))
                .append('\n')
                .append("Hit Ratio: ")
                .append(String.format("%,.2f%%", metrics.getHitRatio() * 100))
                .append('\n');

        addMetrics("Gets    ", metrics.getGetMetrics(), sb);
        addMetrics("Puts    ", metrics.getPutMetrics(), sb);
        addMetrics("Removes ", metrics.getRemoveMetrics(), sb);
    }


    /**
     * Adds metrics details (in millis) for a given
     * {@link TimerSnapshot} to the given {@link StringBuilder}.
     *
     * @param title     title to give the output
     * @param snapshot  {@link TimerSnapshot} to retrieve metrics from
     * @param sb        {@link StringBuilder} to append to
     */
    private void addMetrics(String        title,
                            TimerSnapshot snapshot,
                            StringBuilder sb)
    {
        final double NANNOS = 1000000.0d;

        sb.append(title).append(":").append(" count=")
                .append(snapshot.getCount()).append(", average=")
                .append(String.format("%.4fms", snapshot.getAverage() / NANNOS))
                .append(", min=").append(String.format("%.4fms", snapshot.getMin() / NANNOS))
                .append(", max=")
                .append(String.format("%.4fms", snapshot.getMax() / NANNOS))
                .append(", stddev=")
                .append(String.format("%.4fms", snapshot.getStdDev() / NANNOS))
                .append(", 50th percentile=")
                .append(String.format("%.4fms", snapshot.get50thPercentile() / NANNOS))
                .append(", 75th percentile=")
                .append(String.format("%.4f ms", snapshot.get75thPercentile() / NANNOS))
                .append(", 95th percentile=")
                .append(String.format("%.4f ms", snapshot.get95thPercentile() / NANNOS))
                .append(", 99th percentile=")
                .append(String.format("%.4f ms", snapshot.get99thPercentile() / NANNOS))
                .append('\n')
                .append("          mean rate=")
                .append(String.format("%,.1f/sec", snapshot.getMeanRate()))
                .append(", 1 min rate=")
                .append(String.format("%,.1f/sec", snapshot.getOneMinuteRate()))
                .append(", 5 min rate=")
                .append(String.format("%,.1f/sec", snapshot.getFiveMinuteRate()))
                .append(", 15 min rate=")
                .append(String.format("%,.1f/sec", snapshot.getFifteenMinuteRate()))
                .append('\n');
    }


    /**
     * Returns a "randomish" String for nDataSize size.
     *
     * @param dataSize the size to make the String
     *
     * @return the new String
     */
    protected String getRandomValue(int dataSize)
    {
        char[] aChars = new char[dataSize];
        int    nLen   = RANDOM_DATA.length;

        for (int i = 0; i < dataSize; i++)
        {
            aChars[i] = RANDOM_DATA[i % nLen];
        }

        return new String(aChars);
    }


    /**
     * Logs a given message.
     *
     * @param message the message to log
     */
    public static void log(String message)
    {
        System.out.println(message);
    }


    /**
     * Logs a given message with indentation.
     *
     * @param message the message to log
     */
    private static void logIndent(String message)
    {
        System.out.println(" - " + message);
    }


    /**
     * Logs a header message.
     *
     * @param message the message to log
     */
    public static void header(String message)
    {
        log("\n**** " + message + " ****");
    }


    /**
     * Displays the value for a given key and cache
     *
     * @param key   key to retrieve
     * @param cache cache to use
     */
    private static void valueIs(String key,
                                Cache  cache)
    {
        logIndent(String.format("Value of key [%s] is [%s]", key, cache.get(key)));
    }


    /**
     * Outputs the metrics for the given cache.
     *
     * @param cache cache to output metrics for
     */
    private static void dumpMetrics(Cache cache)
    {
        // sleep for long enough for async metrics to be updated
        sleep(2000L);
        log("");
        log(cache.getMetrics().toString());
    }


    /**
     * Sleeps for a given number of millis and display a message.
     *
     * @param message message to display
     * @param millis  number of millis to sleep for
     */
    private static void sleep(String message,
                              long   millis)
    {
        logIndent(message);
        sleep(millis);
    }


    /**
     * Sleeps for a given number of millis.
     *
     * @param millis number of millis to sleep for
     */
    private static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
        }
    }


    /**
     * Returns the {@link Session} to access the cache.
     *
     * @return the {@link Session}
     */
    private Session getSession()
    {
        return session;
    }


    /**
     * Customer class for POJO example.
     * Note: Does not need to implement java.io.Serialzable.
     */
    public static class Customer
    {
        /**
         * Unique identifier for a Customer.
         */
        private String id;

        /**
         * Name of the customer.
         */
        private String name;

        /**
         * Current balance for a customer.
         */
        private double balance;


        /**
         * No-args constructor required for JSON.
         */
        public Customer()
        {
        }


        /**
         * Constructs a new Customer and default the balance to zero.
         *
         * @param id   id of the customer
         * @param name name of the customer.
         */
        public Customer(String id,
                        String name)
        {
            this.id      = id;
            this.name    = name;
            this.balance = 0;
        }


        /**
         * Returns the customer id.
         *
         * @return the customer id
         */
        public String getId()
        {
            return id;
        }


        /**
         * Returns the customer name.
         *
         * @return the customer name
         */
        public String getName()
        {
            return name;
        }


        /**
         * Sets the customer name
         *
         * @param name new customer name
         */
        public void setName(String name)
        {
            this.name = name;
        }


        /**
         * Returns the customer balance.
         *
         * @return the customer balance
         */
        public double getBalance()
        {
            return balance;
        }


        /**
         * Adjusts the customer balance.
         *
         * @param dValue the amount (+ve or -ve) to adjust by
         */
        public void adjustBalance(double dValue)
        {
            balance += dValue;
        }


        @Override
        public String toString()
        {
            return "Customer{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", balance=" + balance + '}';
        }


        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            Customer customer = (Customer) o;

            if (Double.compare(customer.balance, balance) != 0)
            {
                return false;
            }

            if (id != null ? !id.equals(customer.id) : customer.id != null)
            {
                return false;
            }

            return name != null ? name.equals(customer.name) : customer.name == null;
        }


        @Override
        public int hashCode()
        {
            int  result;
            long temp;

            result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            temp   = Double.doubleToLongBits(balance);
            result = 31 * result + (int) (temp ^ (temp >>> 32));

            return result;
        }
    }


    /**
     * An example {@link CacheLoader} implementation that simulates loading from a
     * cache store.
     */
    public static class CustomerCacheLoader implements CacheLoader<Customer>
    {
        /**
         * Number of loads.
         */
        private long loads = 0L;

        /**
         * The suffix to append to the name when "loading".
         */
        private String suffix;


        /**
         * Creates a cache loader with the given suffix to append on "load".
         *
         * @param suffix the suffix
         */
        public CustomerCacheLoader(String suffix)
        {
            this.suffix = suffix;
        }


        @Override
        public Customer load(String key)
        {
            sleep(100L);
            loads++;

            return new Customer(key, key + this.suffix);
        }


        /**
         * Sets the suffix for new "load" operations.
         *
         * @param sSuffix the suffix
         */
        public void setSuffix(String sSuffix)
        {
            this.suffix = sSuffix;
        }


        /**
         * Returns the number of loads.
         *
         * @return the number of loads.
         */
        public long getLoads()
        {
            return loads;
        }
    }
}
