Warehouse Module
================

To build the warehouse WAR and deploy it to a running Tomcat instance, run:

<code>
./grade clean assemble cargoRunLocal
</code>

To view the API endpoints in the browser via swagger click [here](http://localhost:8085/warehouse). To execute the integration tests against the running local instance execute:

<code>
./gradle integrationLocal
</code>

To execute unit tests only, you don't need to have the API deployed to a container, you can just run:

<code>
./gradle clean test
</code>