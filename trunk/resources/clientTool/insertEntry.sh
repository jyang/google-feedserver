# insertEntry.sh {feedId} {entryFilePath}

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op insert -url $FSCT_FEED_BASE/$1 -username $FSCT_USER_NAME -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL -entryFilePath $2
