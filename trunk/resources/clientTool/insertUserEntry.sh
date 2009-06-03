# insertUserEntry.sh {feedId} {entryFilePath}

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op insert -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/$1 -username $FSCT_USER_NAME -password $FSCT_PASSWORD -entryFilePath $2 -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
