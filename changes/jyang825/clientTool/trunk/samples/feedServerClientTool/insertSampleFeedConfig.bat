@echo off
call setupEnv.bat
java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedServerClientTool -op insert -url %FSCT_FEED_CONFIG_FEED% -username %FSCT_USER_NAME% -password %FSCT_PASSWORD% -entryFilePath sampleFeedConfig.xml
