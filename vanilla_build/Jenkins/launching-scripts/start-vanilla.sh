#!/bin/sh
JAVA_OPTS="$JAVA_OPTS -Dlog4j.debug=true -Dbpm.vanilla.configurationFile=${PWD}/vanilla-conf/vanilla.properties"
echo $JAVA_OPTS

JAVA_HOME="cygdrive/c/Java/jdk1.6.0_20"
VANILLA_STARTER="bpm.vanilla.starter.jar"
VANILLA_STARTER_CLASS="bpm.vanilla.starter.VanillaConfUpdater"

java $JAVA_OPTS -cp  $VANILLA_STARTER $VANILLA_STARTER_CLASS
cd ./bin
#pwd
sh catalina.sh start
