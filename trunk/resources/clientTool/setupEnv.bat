set FSCT_BASE_DIR=..\..
set FSCT_LIB_DIR=%FSCT_BASE_DIR%\lib
set FSCT_DIST_DIR=%FSCT_BASE_DIR%\dist
set FSCT_CLASSPATH=%FSCT_DIST_DIR%/google-feedserver-java-client-2.0.jar;%FSCT_LIB_DIR%/gdata-client-1.0.jar;%FSCT_LIB_DIR%/log4j-1.2.14.jar;%FSCT_LIB_DIR%/commons-cli-1.1.jar;%FSCT_LIB_DIR%/commons-lang-2.4.jar;%FSCT_LIB_DIR%/commons-beanutils-core-1.8.0.jar;%FSCT_LIB_DIR%/commons-logging-1.0.4.jar;.

set FSCT_DOMAIN=example.com
set FSCT_USER_NAME=test_user@%FSCT_DOMAIN%

rem Use with on-prem Google FeedServer
rem set FSCT_FEED_BASE=http://localhost:8080/%FSCT_DOMAIN%
rem set FSCT_USER_FEED_BASE=http://localhost:8080/%FSCT_DOMAIN%/user
rem set SERVICE_NAME=test
rem set AUTHN_URL_PROTOCOL=http
rem set AUTHN_URL=localhost:8080

rem Use with hosted Google FeedServer
set FSCT_FEED_BASE=http://feedserver-enterprise.googleusercontent.com/a/%FSCT_DOMAIN%/g
set FSCT_USER_FEED_BASE=http://feedserver-enterprise.googleusercontent.com/a/%FSCT_DOMAIN%/user
set SERVICE_NAME=esp
set AUTHN_URL_PROTOCOL=https
set AUTHN_URL=www.google.com

