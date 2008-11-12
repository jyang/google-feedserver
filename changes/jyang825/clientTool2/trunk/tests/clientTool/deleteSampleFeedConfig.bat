@echo off
call setupEnv.bat
java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedServerClientTool -op delete -url %FSCT_FEED_CONFIG_FEED%/contact -username %FSCT_USER_NAME% -password %FSCT_PASSWORD%
