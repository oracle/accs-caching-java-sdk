## LocalSessionProvider Example

This directory contains an example showing how to use the
the client API against a LocalSessionProvider without having to deploy to ACCS.

Ensure you are in the `cache-client-examples/local-session-provider-example` directory and run the following command to build the examples:


   ```
   mvn clean install
   ```

Issue the following to run the example using the LocalSessionProvider, which runs
the full API against a local/in-memory cache.


   ```
   mvn exec:exec
   ```
