OpenSAMLSP Setup and Tutorial

Prerequisites:
Java 1.7.0 installed.
Maven 3.0.4 installed.

Verify if Java and Maven are installed properly by opening a CLI and running these commands : 
java -version 
mvn -version

Portecle app downloaded from http://sourceforge.net/projects/portecle/ to edit Java keystore files
The download is a zip file, you can just extract all files and copy the resulting folder to a location of your choice.

1. 	Get the code for the test app:
	The PowerSchool district administrator will provide you with an archive containing the source code in maven format, along with a PowerSchool SSL certificate 
	and an SSO certificate/private key pair for use in developing service provider software. Follow the instructions below to get the sample app up and running 
	using PowerSchool as the federated IdP for your service provider. 
   
2. 	Extract the contents of the OpenSamlSP archive into your desired workspace.
   
3.  Install Plugin: In PowerSchool, System-->System Settings-->Plugin Management-->Import/Install then select the test plugin file C:\path\to\OpenSamlSP\spring-security-saml-plugin.xml
    Make a note of the IdP Entity ID value. Make a note of the IdP metadata URL value. Enable the plugin.

4. 	Import the SSL certificate form PS into test apps trust store.

	a. Launch Portecle: java -jar C:\path\to\portecle.jar
	b. In the UI, Choose File > Open Keystore File...
	c. Navigate to C:\path\to\OpenSamlSP\apache-tomcat-7\security
	d. Choose pstruststore.jks (trust store password is "password")
	e. When truststore is open choose Tools > Import Trusted Certificate
	f. Navigate to location of the certificates provided to you by the district administrator. 
	g. Make sure to select the SSL certificate.
	h. OK to import it.
	i. Save changes.
   
5. 	Set an environment variable: CATALINA_HOME to the included tomcat container using the path where your OpenSamlSP folder is located
     set CATALINA_HOME=C:\path\to\OpenSamlSP\apache-tomcat-7

6. 	Open a command tool (or other batch file editor) and edit C:\path\to\OpenSamlSP\apache-tomcat-7\bin\setenv.bat. 
	Make sure that the values in the following variables match the values in the PowerSchool plugin (step 3):
	 * set CATALINA_OPTS=%CATALINA_OPTS% -DspEntityID="springsecuritysaml"
	 * set CATALINA_OPTS=%CATALINA_OPTS% -DidpEntityID="https://localhost/springsecuritysaml" 
	 * set CATALINA_OPTS=%CATALINA_OPTS% -DidpMetadataURL="https://localhost/powerschool-saml-sso/metadata/customIDPMetadataAction.action?partner=springsecuritysaml"
	 
7. 	From a CLI: cd C:\path\to\OpenSamlSP\

8. Enter: run_app 
	(This will use maven and build the app, then run the app. Subsequent executions will not rebuild the app, but will simply run the app, unless you use 
	the clean_app script to clean it. The app will run in the forground of the CLI. Cmd-C to exit.)
   
9. Confirm the app runs without major errors.

10. Test Single Sign On: click the "plug" icon in the breadcrumbs banner and click the "SpringSAML Demo" link, or click the left navBar "SpringSAML Demo" link.
	SSO should succeed with a diplay showing SAML XML response with attributes passed. Check attributes are correct for current PowerSchool user.
	To repeat process click the Logout link at the bottom of the page.

The source code uses Spring Security and OpenSaml for Java packages. You can customize many of the settings by editing the securityContext.xml which acts as the application context file. That said, the example is really only 
intended to illustrate the specific requirements for federating with PowerSchool as a SAML IdP. 

