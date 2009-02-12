FSCT_BASE_DIR=../../..
FSCT_LIB_DIR=$FSCT_BASE_DIR/lib
FSCT_DIST_DIR=$FSCT_BASE_DIR/dist
FSCT_CLASSPATH=$FSCT_DIST_DIR/google-feedserver-java-client-2.0.jar:$FSCT_LIB_DIR/gdata-client-1.0.jar:$FSCT_LIB_DIR/log4j-1.2.14.jar:$FSCT_LIB_DIR/commons-cli-1.1.jar:$FSCT_LIB_DIR/commons-lang-2.4.jar:$FSCT_LIB_DIR/commons-beanutils-core-1.8.0.jar:$FSCT_LIB_DIR/commons-logging-1.0.4.jar

FSCT_DOMAIN=example.com
FSCT_FEED_BASE=http://localhost:8080/$FSCT_DOMAIN
FSCT_ADAPTER_CONFIG_FEED=$FSCT_FEED_BASE/AdapterConfig
FSCT_FEED_CONFIG_FEED=$FSCT_FEED_BASE/FeedConfig
SERVICE_NAME=test
AUTHN_URL_PROTOCOL=http
AUTHN_URL=localhost:8080
