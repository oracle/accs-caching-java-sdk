# Application Container Cloud Service Application Cache Java API

This project contains the source code for the Java client library  
to allow applications access caches in the Application Container Cloud Service (ACCS).

To create an Application Cache, see the [Oracle Cloud Documentation](http://www.oracle.com/pls/topic/lookup?ctx=cloud&id=CACHE-GUID-9E86E21F-E84C-4F2D-B101-FD461C8A0455)

## Prerequisites

Install these before building the Java client library:

1. Java 8 SE Development Kit or Runtime environment

     - [Java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
     - [Java SE Runtime Environment](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

2. Maven 3.2.5 or above installed and configured

   - https://maven.apache.org/download.git


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
the client API against a LocalSessionProvider without having to deploy to ACCS.

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

For further information on using the JAVA API within your ACCS applications, see
       the [Oracle Cloud Documentation](http://www.oracle.com/pls/topic/lookup?ctx=cloud&id=CACHE-GUID-9E86E21F-E84C-4F2D-B101-FD461C8A0455).
