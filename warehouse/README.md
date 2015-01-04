Warehouse Module
================

To build the warehouse WAR and deploy it to a running Jetty (Tomcat coming soon) instance, run:

<code>
./grade clean assemble build appRun
</code>

To view the API endpoints in the browser via swagger click [here](http://localhost:8085/warehouse).

To build the warehouse WAR, deploy it to a new container, start the container,
run the integration tests against the container, and then stop the container with one command, run:

<code>
./gradle clean assemble build integrationLocalWithContainer
</code>

To execute the integration tests (WITHOUT starting a container) against the running local instance execute:

<code>
./gradle integrationLocal
</code>

To execute unit tests only, you don't need to have the API deployed to a container, you can just run:

<code>
./gradle clean test
</code>

