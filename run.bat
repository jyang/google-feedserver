@echo off
setlocal EnableDelayedExpansion

rem Add all jars under 'lib' and 'dist' to the classpath
FOR %%i IN ("lib\*.jar") DO set CLASSPATH=!CLASSPATH!;%%i
FOR %%j IN ("dist\*.jar") DO set CLASSPATH=!CLASSPATH!;%%j

set CLASSPATH=%CLASSPATH%;.

rem Add "conf" dir to CLASSPATH. Thats where adapter and feedserver config files are stored
set CLASSPATH=%CLASSPATH%;conf
rem Add the resources/clientTool to the classpath for including the log4j.properties
set CLASSPATH=%CLASSPATH%;resources/clientTool

java -server -classpath "%CLASSPATH%" com.google.feedserver.server.jetty.Main %*
