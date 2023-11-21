#!/bin/sh
JAVA_OPTS="$JAVA_OPTS -Dbpm.vanilla.configurationFile=${PWD}/aklabox-conf/aklabox.properties"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=${PWD}/aklabox-conf/log.xml"
echo $JAVA_OPTS

echo "Deleting catalina.out"

cp logs/catalina.out logs/catalina.out.$(date +"%Y_%m_%d_%H_%M_%S").log
rm -rf logs/catalina.out

cd solr-4.4.0/example/
nohup java -jar start.jar > solr.log 2>&1 &
cd ../..

AKLABOX_STARTER="bpm.vanilla.starter.jar"
AKLABOX_STARTER_CLASS="bpm.vanilla.starter.VanillaConfUpdater"
AKLABOX_LAUNCHER_PATH="$PWD"
java $JAVA_OPTS -cp  $AKLABOX_STARTER $AKLABOX_STARTER_CLASS
echo $AKLABOX_LAUNCHER_PATH

if [ "$1" = "start" ] ; then
	sh bin/catalina.sh start $AKLABOX_LAUNCHER_PATH
elif [ "$1" = "run" ]; then
	sh bin/catalina.sh run $AKLABOX_LAUNCHER_PATH
else
	sh bin/catalina.sh run $AKLABOX_LAUNCHER_PATH
fi
