# updateEntry.sh {feedId} {entryId/entryName} {entryFilePath}

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op update -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/$1/$2 -username $FSCT_USER_NAME -entryFilePath $3 -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
