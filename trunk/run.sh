#!/bin/bash
#
# Copyright 2007 Google Inc. All Rights Reserved.
# Author: cva@google.com (Chris Anderson)

# build up a classpath containing all the jars in lib/ and dist/
cd `dirname $0`
for jar in lib/*.jar build/jar/*.jar dist/*.jar; do
  CLASSPATH=${CLASSPATH}${CLASSPATH:+:}$jar
done

CLASSPATH=$CLASSPATH:conf

# and run jetty.Main
if [ $# -ne 1 ]
then
  echo "NO port# to start Jetty on is specified."
  java -classpath $CLASSPATH com.google.feedserver.server.jetty.Main
else
  echo "Starting Jetty on port#=$1"
  java -classpath $CLASSPATH com.google.feedserver.server.jetty.Main --port $1
fi
