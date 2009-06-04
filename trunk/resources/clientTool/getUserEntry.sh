# getEntry.sh {feedId} {entryId/entryName}

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op getEntry -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/$1/$2?nocache=1 -username $FSCT_USER_NAME -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
