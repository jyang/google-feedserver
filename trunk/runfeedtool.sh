#!/bin/bash
#
# * Copyright 2008 Google Inc.
# *
# * This program is free software; you can redistribute it and/or
# * modify it under the terms of the GNU General Public License
# * as published by the Free Software Foundation; either version 2
# * of the License, or (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program; if not, write to the Free Software
# * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#
# Runs client with appropriate class path.

WOODSTOCK_HOME=`pwd`

export PATH=$JAVA_HOME/bin/:$PATH 

export CLASSPATH=`pwd`/src/java
for jar in `find . -type f -name \*.jar`
do export CLASSPATH=$CLASSPATH:$WOODSTOCK_HOME/$jar
done

JVM_ARGS="-Djava.net.preferIPv4Stack=true"   

java com.google.feedserver.tools.SampleFeedTool $*
