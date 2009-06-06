# getUserEntry.sh {feedId} {entryId/entryName} -password {password}
#
# If "/" is part of entry name, please escape it to %2F.  For example, if an
# entry name is "PrivateGadgetSpec/hello.xml", use
# "PrivateGadgetSpec%2Fhello.xml".

source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op getEntry -url $FSCT_USER_FEED_BASE/$FSCT_USER_NAME/g/$1/$2?nocache=1 -username $FSCT_USER_NAME $3 $4 -serviceName $SERVICE_NAME -authnURLProtocol $AUTHN_URL_PROTOCOL -authnURL $AUTHN_URL
