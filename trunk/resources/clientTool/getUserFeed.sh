source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op getFeed -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/$1?nocache=1 -username $FSCT_USER_NAME -password $FSCT_PASSWORD -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
