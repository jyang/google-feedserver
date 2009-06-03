# setAcl.sh <feedName>(/<entryName>)* ({c|r|u|d}{+|-}<principal>,)+
#
# e.g.: setAcl.sh Cafe r-one@example.com,crud+admin@example.com
# e.g.: setAcl.sh Cafe/deli r+one@example.com,crud-admin@example.com

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientAclTool -op setAcl -url $FSCT_FEED_BASE/acl -username $FSCT_USER_NAME -password $FSCT_PASSWORD -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL -resource $1 -acl $2
