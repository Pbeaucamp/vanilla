echo off
set LIBS="libs/vanillaserver_dependancies/commons-io-1.4.jar;libs/vanillaserver_dependancies/dom4j.jar;libs/bpm.vanilla.commons.communication_4.0.0.1.jar;libs/bpm.vanilla.server.client_4.0.0.1.jar;libs/jxl.jar;libs/bpm.vanilla.server.client.commandline.jar"
set CLASS=bpm.vanilla.server.client.commandline.profiing.ReportProfilingTool
set CONFIGURATION_FILE=%1
rem defaultCOnfigurationFIle= profiling_report.properties
echo off
java -Dprofiling.configurationFile=%CONFIGURATION_FILE% -cp  %LIBS% %CLASS% 

