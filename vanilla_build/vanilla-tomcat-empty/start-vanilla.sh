#!/bin/sh
ParamCommand2="$2";
case "$ParamCommand2" in
  *-Dbpm.vanilla.configurationFile*) echo "Property -Dbpm.vanilla.configurationFile already exist, no override"; JAVA_OPTS="$JAVA_OPTS $ParamCommand2" ;;
  *)         echo "Property -Dbpm.vanilla.configurationFile does not exist, setting it."; JAVA_OPTS="$JAVA_OPTS -Dbpm.vanilla.configurationFile=${PWD}/vanilla-conf/vanilla.properties  -Dbpm.update.manager.configurationFile=${PWD}/vanilla-conf/update_manager.properties" ;;
esac

if [ -z "$3" ]
  then
    CATALINA_PID="/var/run/vanilla/vanilla6.pid"
else
	CATALINA_PID="$3"
fi

JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=${PWD}/vanilla-conf/log.xml"
echo $JAVA_OPTS

VANILLA_STARTER="bpm.vanilla.starter.jar"
VANILLA_STARTER_CLASS="bpm.vanilla.starter.VanillaConfUpdater"
VANILLA_LAUNCHER_PATH="$PWD"
java $JAVA_OPTS -cp  $VANILLA_STARTER $VANILLA_STARTER_CLASS
echo $VANILLA_LAUNCHER_PATH

if [ "$1" = "start" ] ; then
	sh bin/catalina.sh start $VANILLA_LAUNCHER_PATH $ParamCommand2 '' $CATALINA_PID
elif [ "$1" = "run" ]; then
	sh bin/catalina.sh run $VANILLA_LAUNCHER_PATH $ParamCommand2
else
	sh bin/catalina.sh run $VANILLA_LAUNCHER_PATH $ParamCommand2
fi
