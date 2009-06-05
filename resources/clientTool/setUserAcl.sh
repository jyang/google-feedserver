# setUserAcl.sh <feedName>(/<entryName>)* ({c|r|u|d}{+|-}<principal>,)+
#
# e.g.: setUserAcl.sh Cafe r-one@example.com,crud+admin@example.com
# e.g.: setUserAcl.sh Cafe/deli r+one@example.com,crud-admin@example.com

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientAclTool -op setAcl -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/acl -username $FSCT_USER_NAME -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL -resource $1 -acl $2
