# Java Client Caching SDK for Oracle Application Container Cloud Service

[![Build Status](https://travis-ci.org/oracle/accs-caching-java-sdk.svg?branch=master)](https://travis-ci.org/oracle/accs-caching-java-sdk)

This project contains the source code for the Java client library  
to allow apps access caches in the Application Container Cloud Service (ACCS) of Oracle's Cloud Platform.

To create an Application Cache, see the [documentation](http://www.oracle.com/pls/topic/lookup?ctx=cloud&id=CACHE-GUID-9E86E21F-E84C-4F2D-B101-FD461C8A0455)

## Prerequisites

Install these before building the Java client library:

1. Java SE Development Kit (JDK)

     - [Java SE Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
     - Install the latest Java SE JDK available

2. Maven 3.2.5 or above installed and configured

   - [Download instructions](https://maven.apache.org/download.cgi)
   - [Installation instructions](https://maven.apache.org/install.html)


## Build Instructions

Build the Java client by running these commands:

  - Clone the repository

   ```
   git clone https://github.com/oracle/accs-caching-java-sdk.git
   ```

  - Change to the directory

   ```
   cd accs-caching-java-sdk
   ```

  - Build the client

   ```
   mvn clean install
   ```

   If you do not wish to run the local functional tests, run this command:

   ```
   mvn clean install -DskipTests
   ```

   If you wish to create a 'shaded' jar, run this command:

   ```
   mvn clean install -DskipTests -P shade
   ```

## Running the examples

The directory ``cache-client-examples`` contains two sub-projects that show how to use the client API.

### Deployable ACCS example

The sub-directory [``cache-client-examples/appcache-example``](cache-client-examples/appcache-example) contains an example using the client API which can be deployed to ACCS.

### LocalSessionProvider Example

The sub-directory  [``cache-client-examples/local-session-provider-example``](cache-client-examples/local-session-provider-example) contains an example showing how to use the
the client API (including full API functionality) against a LocalSessionProvider without having to deploy to ACCS.

## Referencing the API from Maven Projects

To add a dependency on the cache-client-api in your Maven projects, include
       this dependency.

       ```
        <dependency>
            <groupId>com.oracle.cloud.caching</groupId>
            <artifactId>cache-client-api</artifactId>
            <version>1.0.0</version>
        </dependency>
       ```

## Further Information

For further information on using the Java API within your applications deployed on ACCS, see 
the [documentation](http://www.oracle.com/pls/topic/lookup?ctx=cloud&id=CACHE-GUID-9E86E21F-E84C-4F2D-B101-FD461C8A0455).

# <a name="contrib"></a> Contributing

This is an open source project and we welcome contributions. See [CONTRIBUTING](./CONTRIBUTING.md) for details.

# <a name="license"></a> License

You may not use the identified files except in compliance with the Universal Permissive License (UPL), Version 1.0 (the "License.")

You may obtain a copy of the License at https://opensource.org/licenses/UPL. A copy of the license is also reproduced in [LICENSE.md](./LICENSE.md)
