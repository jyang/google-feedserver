source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op delete -authnServiceName $AUTHN_SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL -url $FSCT_FEED_BASE/$1
