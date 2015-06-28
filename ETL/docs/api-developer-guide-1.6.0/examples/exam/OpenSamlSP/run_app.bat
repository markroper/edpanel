@echo off

if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

if exist ".\apache-tomcat-7\webapps\spring-security-saml2-sp.war" goto okBuilt
echo building app first....
call mvn package -DskipTests

:okBuilt
%CATALINA_HOME%\bin\catalina run

:end
