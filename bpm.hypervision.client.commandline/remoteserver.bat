echo off
set LIBS="libs/vanillaserver_dependancies/commons-io-1.4.jar;libs/vanillaserver_dependancies/dom4j.jar;libs/bpm.vanilla.commons.communication_4.0.0.1.jar;libs/bpm.vanilla.server.client_4.0.0.1.jar;libs/bpm.vanilla.server.client.commandline.jar"
set CLASS=bpm.vanilla.server.client.commandline.ReportingServer
set CMD=%1
set SERVERTYPE=%2
set URL=%3
set LOGIN=%4
set PASSWORD=%5
set LIST=%6
set GROUPS=%7

java -cp  %LIBS% %CLASS% %CMD% %SERVERTYPE% %URL% %LOGIN% %PASSWORD% %LIST% %GROUPS%

