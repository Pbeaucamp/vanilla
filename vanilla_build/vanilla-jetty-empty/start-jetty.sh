#!/bin/bash

MEMORY="-Xms256m -Xmx4096m"
PORT="7070"

DEFAULT_ARGS="-Dlog4j.debug=true"
VANILLA_PROPERTIES="-Dbpm.vanilla.configurationFile=vanilla/vanilla-conf/vanilla.properties"
LOGBACK_PROPERTIES="-Dlogback.configurationFile=vanilla/vanilla-conf/logback.xml"

ARGUMENTS="${MEMORY} ${DEFAULT_ARGS} ${VANILLA_PROPERTIES} ${LOGBACK_PROPERTIES}"

nohup java ${ARGUMENTS} -jar start.jar -Djetty.port=${PORT} &> logs/jetty.out &
