@echo on
rem set JAVA_OPTS=-Dlog4j.configuration=log.xml -Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/aklabox-conf/aklabox.properties
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/aklabox-conf/aklabox.properties -Dlog4j.configuration=%cd%/aklabox-conf/log.xml -Doffice.home="C:/Program Files (x86)/OpenOffice 4/"
echo %JAVA_OPTS%
echo %JAVA_HOME%

set AKLABOX_LIBS="bpm.vanilla.starter.jar"
set AKLABOX_CLASS=bpm.vanilla.starter.VanillaConfUpdater
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/aklabox-conf/aklabox.properties -Dlog4j.configuration=%cd%/aklabox-conf/log.xml -Doffice.home="C:/Program Files (x86)/OpenOffice 4/"
echo %JAVA_OPTS%
CALL "%JAVA_HOME%/bin/java.exe" %JAVA_OPTS% -cp  %AKLABOX_LIBS% %AKLABOX_CLASS%
echo update Done!!

start solr.bat

if ""%1"" == ""start"" goto doStart

:doStart
bin/catalina.bat start
goto end

bin/catalina.bat run

:end
echo aklabox is ready
