@echo off
set FSCT_BASE_DIR=%0\..
set FSCT_BIN_DIR=%FSCT_BASE_DIR%
set FSCT_LIB_DIR=%FSCT_BASE_DIR%\lib
set FSCT_DIST_DIR=%FSCT_BASE_DIR%\dist
set FSCT_CLASSPATH=%FSCT_DIST_DIR%\google-feedserver-java-client-2.0.jar;%FSCT_LIB_DIR%\gdata-client-1.0.jar;%FSCT_LIB_DIR%\log4j-1.2.14.jar;%FSCT_LIB_DIR%\commons-cli-1.1.jar;%FSCT_LIB_DIR%\commons-lang-2.4.jar;%FSCT_LIB_DIR%\commons-beanutils-core-1.8.0.jar;%FSCT_LIB_DIR%\commons-beanutils-1.8.0.jar;%FSCT_LIB_DIR%\commons-logging-1.0.4.jar;%FSCT_LIB_DIR%\commons-collections-3.2.1.jar;%FSCT_LIB_DIR%\jline-0.9.94.jar;%FSCT_BIN_DIR%

java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedClient %*
