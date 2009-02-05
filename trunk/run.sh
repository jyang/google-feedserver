#!/bin/bash
# build up a classpath containing all the jars in lib/ and dist/
for jar in lib/*.jar dist/*.jar; do
  CLASSPATH=${CLASSPATH}:$jar
done

#add "conf" dir to CLASSPATH. thats where adapter and feedserver config files are at.
CLASSPATH=$CLASSPATH:conf

# number of args can be 0 or 2. not 1.
# because in SimpleCommandLineParser.java file, it sets port, uri separately. For these 2 to be
# in sync with each other, either the user supplies both or none.
# and run jetty.Main

if [ $# -eq 1 ]
then
   if [ $1 == "help" ]
   then
     echo "Usage: $0 --port=<number> --uri=http://host:port"
     exit 0
   fi
fi
java -classpath $CLASSPATH com.google.feedserver.server.jetty.Main $*
