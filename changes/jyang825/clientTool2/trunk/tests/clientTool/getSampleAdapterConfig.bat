@echo off
call setupEnv.bat
java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedServerClientTool -op getEntry -url %FSCT_ADAPTER_CONFIG_FEED%/sampleDbAdapter -username %FSCT_USER_NAME% -password %FSCT_PASSWORD%
