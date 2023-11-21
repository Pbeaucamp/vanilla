SET CURRENT_PATH=%~dp0
SET MEMORY=-Xms256m -Xmx1024m
SET PORT=7171

#SET ALLOWED_JAVA_PACKAGES=-Dorg.osgi.framework.bootdelegation=org.w3c.dom,javax.*,org.xml.*
SET ALLOWED_JAVA_PACKAGES=-Dosgi.compatibility.bootdelegation=true
SET DEFAULT_ARGS=-Declipse.ignoreApp=true -Dosgi.noShutdown=true -Dlog4j.debug=true -Dorg.eclipse.equinox.http.jetty.http.port=%PORT%
SET CONTEXT=-Dorg.eclipse.equinox.http.jetty.context.path=/VanillaRuntime
SET VANILLA_PROPERTIES=-Dbpm.vanilla.configurationFile=%CURRENT_PATH%vanilla/vanilla-conf/vanilla.properties
SET LOGBACK_PROPERTIES=-Dlogback.configurationFile=%CURRENT_PATH%vanilla/vanilla-conf/logback.xml

SET ARGUMENTS=%MEMORY% %ALLOWED_JAVA_PACKAGES% %DEFAULT_ARGS% %CONTEXT% %VANILLA_PROPERTIES% %LOGBACK_PROPERTIES%

java %ARGUMENTS% -jar vanilla/VanillaRuntime/org.eclipse.osgi_3.13.200.v20181130-2106.jar -consoleLog -console