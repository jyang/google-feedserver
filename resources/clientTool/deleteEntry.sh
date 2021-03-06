# deleteEntry.sh {feedName} {entryId/entryName} -password {password}
#
# If "/" is part of entry name, please escape it to %2F.  For example, if an
# entry name is "PrivateGadgetSpec/hello.xml", use
# "PrivateGadgetSpec%2Fhello.xml".

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op delete -url $FSCT_FEED_BASE/$1/$2 -username $FSCT_USER_NAME $3 $4 -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
