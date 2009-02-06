source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op insert -url $FSCT_FEED_BASE/$1 -authnServiceName $AUTHN_SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL -entryFilePath contactFeedEntry.xml 
