#!/bin/bash

MEMORY="-Xms256m -Xmx1024m"
PORT="7171"

ALLOWED_JAVA_PACKAGES="-Dosgi.compatibility.bootdelegation=true"
DEFAULT_ARGS="-Declipse.ignoreApp=true -Dosgi.noShutdown=true -Dlog4j.debug=true -Dorg.eclipse.equinox.http.jetty.http.port=${PORT}"
CONTEXT="-Dorg.eclipse.equinox.http.jetty.context.path=/VanillaRuntime"
VANILLA_PROPERTIES="-Dbpm.vanilla.configurationFile=${PWD}/vanilla/vanilla-conf/vanilla.properties"
LOGBACK_PROPERTIES="-Dlogback.configurationFile=${PWD}/vanilla/vanilla-conf/logback.xml"

ARGUMENTS="${MEMORY} ${ALLOWED_JAVA_PACKAGES} ${DEFAULT_ARGS} ${CONTEXT} ${VANILLA_PROPERTIES} ${LOGBACK_PROPERTIES}"

#echo java ${ARGUMENTS} -jar vanilla/VanillaRuntime/org.eclipse.osgi_3.13.200.v20181130-2106.jar -consoleLog -console
#java ${ARGUMENTS} -jar vanilla/VanillaRuntime/org.eclipse.osgi_3.13.200.v20181130-2106.jar -consoleLog -console
nohup java ${ARGUMENTS} -jar vanilla/VanillaRuntime/org.eclipse.osgi_3.13.200.v20181130-2106.jar -consoleLog -console &> logs/runtime.out &
