@echo on
rem set JAVA_OPTS=-Dlog4j.configuration=log.xml -Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties
echo %JAVA_OPTS%
echo %JAVA_HOME%


set VANILLA_LIBS="bpm.vanilla.starter.jar"
set VANILLA_CLASS=bpm.vanilla.starter.VanillaConfUpdater
set JAVA_OPTS=-Dlog4j.debug=true -Dbpm.vanilla.configurationFile=%cd%/vanilla-conf/vanilla.properties
echo %JAVA_OPTS%
CALL %JAVA_HOME%/bin/java.exe %JAVA_OPTS% -cp  %VANILLA_LIBS% %VANILLA_CLASS%
echo update Done!!

cd .\bin
startup.bat
echo vanilla is ready
