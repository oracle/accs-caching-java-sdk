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

The sub-project cache-client-examples contains an example client that uses 
the client API.

Change to the `cache-client-examples` directory and run the following command to build the examples:


   ```
   mvn clean install
   ```

Issue the following to run the example using the LocalSessionProvider, which runs 
the full API against a local/in-memory cache.


   ```
   mvn exec:exec
   ```
     
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
       
       
