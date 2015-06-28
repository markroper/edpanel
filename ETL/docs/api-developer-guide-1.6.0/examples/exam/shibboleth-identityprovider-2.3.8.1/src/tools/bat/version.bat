@echo off
setlocal

REM We need a JVM
if not defined JAVA_HOME  (
  echo Error: JAVA_HOME is not defined.
  exit /b
)

if not defined JAVACMD (
  set JAVACMD="%JAVA_HOME%\bin\java.exe"
)

if not exist %JAVACMD% (
  echo Error: JAVA_HOME is not defined correctly.
  echo Cannot execute %JAVACMD%
  exit /b
)

if defined CLASSPATH (
  set LOCALCLASSPATH=%CLASSPATH%
)

if not exist "%IDP_HOME%" (
  echo Error: IDP_HOME is not defined correctly.
  exit /b
)

REM add in the dependency .jar files 
for %%i in ("%IDP_HOME%\lib\*.jar") do (
    call "%IDP_HOME%\bin\cpappend.bat" %%i
)

if exist %JAVA_HOME%\lib\tools.jar (
    set LOCALCLASSPATH=%LOCALCLASSPATH%;%JAVA_HOME%\lib\tools.jar
)

if exist %JAVA_HOME%\lib\classes.zip (
    set LOCALCLASSPATH=%LOCALCLASSPATH%;%JAVA_HOME%\lib\classes.zip
)

REM Go to it !

%JAVACMD% -cp "%LOCALCLASSPATH%" edu.internet2.middleware.shibboleth.idp.Version
