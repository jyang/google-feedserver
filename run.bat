@echo off
setlocal EnableDelayedExpansion

rem Add all jars under 'lib' and 'dist' to the classpath
FOR %%i IN ("lib\*.jar") DO set CLASSPATH=!CLASSPATH!;%%i
FOR %%j IN ("dist\*.jar") DO set CLASSPATH=!CLASSPATH!;%%j

set CLASSPATH=%CLASSPATH%;.

rem Add "conf" dir to CLASSPATH. Thats where adapter and feedserver config files are stored
set CLASSPATH=%CLASSPATH%;conf

if X%1==Xhelp (
echo "Usage: %0 --port=<number> --uri=http://host:port authenticated=true|false OAuth_authenticated=true|false"
echo "Specify either authenticated or OAuth_authenticated and not both"
) else (
java -classpath "%CLASSPATH%" com.google.feedserver.server.jetty.StartFeedServerWithJetty %*
)
endlocal





