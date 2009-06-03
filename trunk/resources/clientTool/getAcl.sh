# getAcl.sh <resource>
#
# e.g.: getAcl.sh Cafe
# e.g.: getAcl.sh Cafe/deli

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op getFeed -url $FSCT_FEED_BASE/acl?bq=[resourceRule=$1]\&nocache=1 -username $FSCT_USER_NAME -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
