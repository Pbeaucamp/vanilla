#!/bin/sh
JAVA_OPTS="$JAVA_OPTS -Dbpm.vanilla.configurationFile=${PWD}/vanilla-conf/vanilla.properties"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=${PWD}/vanilla-conf/log.xml"
echo $JAVA_OPTS

echo "Deleting catalina.out"

cp logs/catalina.out logs/catalina.out.$(date +"%Y_%m_%d_%H_%M_%S").log
rm -rf logs/catalina.out

VANILLA_STARTER="bpm.vanilla.starter.jar"
VANILLA_STARTER_CLASS="bpm.vanilla.starter.VanillaConfUpdater"
VANILLA_LAUNCHER_PATH="$PWD"
java $JAVA_OPTS -cp  $VANILLA_STARTER $VANILLA_STARTER_CLASS
echo $VANILLA_LAUNCHER_PATH

if [ "$1" = "start" ] ; then
	sh bin/catalina.sh start $VANILLA_LAUNCHER_PATH
elif [ "$1" = "run" ]; then
	sh bin/catalina.sh run $VANILLA_LAUNCHER_PATH
else
	sh bin/catalina.sh run $VANILLA_LAUNCHER_PATH
fi
