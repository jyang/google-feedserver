set FSCT_BASE_DIR=..\..
set FSCT_LIB_DIR=%FSCT_BASE_DIR%\lib
set FSCT_DIST_DIR=%FSCT_BASE_DIR%\dist
set FSCT_CLASSPATH=%FSCT_DIST_DIR%\google-feedserver-client-2.0.jar;%FSCT_LIB_DIR%\gdata-client-1.0.jar;%FSCT_LIB_DIR%\log4j-1.2.14.jar;%FSCT_LIB_DIR%\commons-cli-1.1.jar;%FSCT_LIB_DIR%\commons-lang-2.4.jar

set FSCT_DOMAIN=joonix.net
set FSCT_USER_NAME=demo1@%FSCT_DOMAIN%
set FSCT_PASSWORD=...

set FSCT_FEED_BASE=http://www.google.com/a/feeds/server/g/domain/%FSCT_DOMAIN%
set FSCT_ADAPTER_CONFIG_FEED=%FSCT_FEED_BASE%/AdapterConfig
set FSCT_FEED_CONFIG_FEED=%FSCT_FEED_BASE%/FeedConfig
