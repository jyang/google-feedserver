#!/bin/bash
# build up a classpath containing all the jars in lib/ and dist/
cd `dirname $0`
for jar in lib/*.jar build/jar/*.jar dist/*.jar; do
  CLASSPATH=${CLASSPATH}${CLASSPATH:+:}$jar
done

CLASSPATH=$CLASSPATH:conf

# and run jetty.Main
if [ $# -ne 1 ]
then
  java -classpath $CLASSPATH com.google.feedserver.server.jetty.Main
else
  java -classpath $CLASSPATH com.google.feedserver.server.jetty.Main --port=$1
fi
