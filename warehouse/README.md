Warehouse Module
================

To build the warehouse WAR and deploy it to a running Jetty (Tomcat coming soon) instance, run:

<code>
gradle clean build appRun
</code>

To build the warehouse WAR and deploy it to a DEBUGGABLE jetty instance, run:

<code>
gradle clean build appRunDebug
</code>

Note: The process will startup normally (not halt) but a debugger can be attached on port 5005.

To view the API endpoints in the browser via swagger click [here](http://localhost:8085/warehouse).

To build the warehouse WAR, deploy it to a new container, start the container,
run the integration tests against the container, and then stop the container with one command, run:

<code>
gradle clean build integrationLocalWithContainer
</code>

To execute the integration tests (WITHOUT starting a container) against the running local instance execute:

<code>
gradle integrationLocal
</code>

To execute unit tests only, you don't need to have the API deployed to a container, you can just run:

<code>
gradle clean test
</code>

###Troubleshooting

* If you try to deploy the service to tomcat but tests are failing or the endpoints are all returning errors, 
try running:

<code>
gradle refillDatabase
</code>

This task blows away and recreates the database tables and re-seeds the
new instance with some test users.

To log in to the deployed webapp, use the following credentials (different credentials provide different roles):

teacher:
doe/password

administrator:
mroper/admin

student
carlos/password