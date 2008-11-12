source ./setupEnv.sh
java -cp $FSCT_CLASSPATH com.google.feedserver.tools.FeedServerClientTool -op update -url $FSCT_ADAPTER_CONFIG_FEED/sampleDbAdapter -username $FSCT_USER_NAME -password $FSCT_PASSWORD -entryFilePath sampleAdapterConfig.xml
