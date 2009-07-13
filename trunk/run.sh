#!/bin/bash
# build up a classpath containing all the jars in lib/ and dist/
for jar in lib/*.jar dist/*.jar; do
  CLASSPATH=${CLASSPATH}:$jar
done

#add "conf" dir to CLASSPATH. thats where adapter and feedserver config files are stored
CLASSPATH=$CLASSPATH:conf
# This is for the log4j property file
CLASSPATH=$CLASSPATH:resources/clientTool

java -server -classpath $CLASSPATH com.google.feedserver.server.jetty.Main $*
