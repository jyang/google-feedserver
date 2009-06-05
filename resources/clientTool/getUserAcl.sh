# getUserAcl.sh <resource>
#
# e.g.: getUserAcl.sh Cafe
# e.g.: getUserAcl.sh Cafe/deli

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op getFeed -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/acl?bq=[resourceRule=$1]\&nocache=1 -username $FSCT_USER_NAME -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
