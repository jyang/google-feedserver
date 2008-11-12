@echo off
call setupEnv.bat
java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedServerClientTool -op getFeed -url %FSCT_FEED_BASE%/contact -username %FSCT_USER_NAME% -password %FSCT_PASSWORD%
