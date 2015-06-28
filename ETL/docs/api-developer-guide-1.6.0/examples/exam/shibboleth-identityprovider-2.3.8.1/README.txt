Welcome to Internet2's Shibboleth

Shibboleth is a federated web authentication and attribute exchange system
based on SAML developed by Internet2 and MACE.

Shibboleth is licensed under the Apache 2.0 license which is provided in the
LICENSE.txt file.

Shibboleth Project Site:
http://shibboleth.internet2.edu/

Shibboleth Documentation Site:
https://wiki.shibboleth.net/confluence/display/SHIB2

Source and binary distributions
http://shibboleth.internet2.edu/downloads

Bug Tracker:
https://bugs.internet2.edu/jira

Installing the 2.3.8.1 (PowerSchool) version 
================================================

Before installation, prepare the following information. You will be prompted for this during the installation process.

Basic Installation Info
- Shibboleth Identity Provider software installation location.
- FQDN or IP for the Shibboleth IdP host machine. This value must have at least two *dots* or a regex function will fail during installation, so use an IP address rather than "localhost" if necessary.
- A password for the generated trust store. ("password" will do fine for development and testing).

PowerSchool Service Provider Info
- The PowerSchool Service Provider entity ID. This value will come from the service provider plugin installed in PowerSchool. 
  This value can be updated later by editing the FileBackedHTTPMetadataProvider XML in conf/relying-party.xml.
- The PowerSchool Service Provider metadata URL. This value will come from the service provider plugin installed in PowerSchool. 
  This value can be updated later by editing the FileBackedHTTPMetadataProvider XML in conf/relying-party.xml.
  
PowerSchool Authentication Info
- Credentials from the PowerSchool installation for the four contexts: admin, teacher, student, guardian. These are to be entered in the following format: username;password

Installation

1.  Open a Command Window as Administrator
2.  Navigate to your sibboleth-identityprovider-2.3.8.1 folder
3.  Run the install script by typing "install" at the command prompt.
4.  Enter your desired Shibboleth Identity Provider software installation location.
5.  Enter the FQDN of the host machine. This can be an IP address or a fully qualified domain name, but it must have at least two *dots* or a regex function in the build script will fail.
    So if you wish to run the IdP on your localhost machine, enter either 127.0.0.1 or the actual IP address of the machine, or map a name with at least two dots in C:\Windows\System32\drivers\etc\hosts
    (e.g. sp.machine.loc  127.0.0.1)
6.  Enter a password for the certificate trust store that will be generated for use by your IdP. For a non-production machine, "password" will do.
7.  Enter the entity ID value for the PowerSchool Service Provider. This value will be available in the detail page of the service provider plugin added to PowerSchool. If you do not know this
    value, use the default for now. This value helps set up a metadata provider in conf/relying-party.xml which you can edit once you've got the service provider plugin properly installed.
8.  Enter the metadata URL value for the PowerSchool Service Provider. This value will be available in the detail page of the service provider plugin added to PowerSchool. If you do not know this
    value, use the default for now. This value helps set up a metadata provider in conf/relying-party.xml which you can edit once you've got the service provider plugin properly installed.
9.  Enter the credentials for an administrator user on your PowerSchool installation. This value should be entered in the following format: username;password and will be used by the simple login
    module associated with the UserPassword handler to allow for simple authentication without the need for standing up an LDAP server or an Active Directory server. You can edit these values 
	or add to them by editing the conf/login.config file located in your Shibboleth Identity Provider software installation location home folder.
10. Enter the credentials for a teacher user on your PowerSchool installation. Format: username;password
11. Enter the credentials for a student user on your PowerSchool installation. Format: username;password
12. Enter the credentials for a guardian user on your PowerSchool installation. Format: username;password
13. Set up a standalone Tomcat server on your host machine. We will be running the IdP through port 8443 using SSL, so it should not interfere with a local PowerSchool installation running 
    on the same machine. If you have an existing Tomcat server already running, take care when copying the Tomcat config files created during this installation.
14. Open the Shibboleth Identity Provider software installation location folder. You should see the following folders"
	apache-tomcat-7/
	bin/
	conf/
	credentials/
	lib/
	logs/
	metadata/
	war/
15. Copy the contents of the apache-tomcat-7/ folder to the home directory of your apache-tomcat-7 installation. Merge the folders and overwrite. NOTE: If you are updating an existing tomcat 
    container, don't overwrite server.xml. Just copy the connector that uses port 8443 into your existing server.xml to preserve your existing settings.
16. Start Tomcat.

Your IdP should start up without errors. If you do not have PowerSchool configured with a service provider plugin, (and you entered the default values), your /metadata/sp-metadata.xml file will 
be a dummy file that does not really connect to anything. This file is necessary to start the IdP with a metadata provider setting in relying-party.xml. If you have connected successfully to 
a proper PowerSchool service provider plugin, and entered a valid metadata URL during installation, this file will be populated with the metadata for that service provider. If during development,
you corrupt or delete your sp-metadata.xml file, you can either rename the sp-metadata.tmpl file or remove the metadata provider XML in relying-party.xml

You can test your install by entering the following URL into a browser:

https://localhost:8443/idp/status (or use your IP address)

This will provide you with information about the IdP. You can also retrieve the metadata for the IdP at:

https://localhost:8443/idp/shibboleth

or 

https://localhost:8443/idp/profile/Metadata/SAML

Once you have started a PowerSchool service provider plugin, and registered that plugin with your sample IdP, you should be able to authenticate by initiating an SSO request. Doing so without a session
in place will cause the IdP login screen to display. Enter one of the username / password pairs that you configured during installation. Authentication should succeed and an AuthnResponse with no attributes
should be sent back to the service provider. Subsequent attempts should succeed because of the existing session. 

At present, there is no logout function in the sample IdP.
