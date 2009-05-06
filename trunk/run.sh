#!/bin/bash
# build up a classpath containing all the jars in lib/ and dist/
for jar in lib/*.jar dist/*.jar; do
  CLASSPATH=${CLASSPATH}:$jar
done

#add "conf" dir to CLASSPATH. thats where adapter and feedserver config files are stored
CLASSPATH=$CLASSPATH:conf
# This is for the log4j property file
CLASSPATH=$CLASSPATH:resources/clientTool

if [ $# -eq 1 ]
then
   if [ $1 == "help" ]
   then
     echo "Usage: $0 --port=<number> --uri=http://host:port authenticated=true|false OAuth_authenticated=true|false"
     echo "Specify either authenticated or OAuth_authenticated and not both"
     exit 0
   fi
fi
java -server -classpath $CLASSPATH com.google.feedserver.server.jetty.StartFeedServerWithJetty $*
