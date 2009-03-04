@echo off
call setupEnv.bat
java -cp %FSCT_CLASSPATH% com.google.feedserver.tools.FeedServerClientTool -op insertGadgetSpec -url %FSCT_FEED_BASE%/%1 -username %FSCT_USER_NAME% -serviceName %SERVICE_NAME% -authnURLProtocol %AUTHN_URL_PROTOCOL% -authnURL %AUTHN_URL% -gadgetSpecEntityFile gadgetSpecEntity.xml -gadgetName %2 -gadgetSpecFile %3