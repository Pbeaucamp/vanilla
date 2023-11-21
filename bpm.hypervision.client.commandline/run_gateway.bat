echo off
set LIBS="libs/vanillaserver_dependancies/commons-io-1.4.jar;libs/vanillaserver_dependancies/dom4j.jar;libs/bpm.vanilla.commons.communication_4.0.0.1.jar;libs/bpm.vanilla.server.client_4.0.0.1.jar;libs/bpm.vanilla.server.client.commandline.jar"
set CLASS=bpm.vanilla.server.client.commandline.RunSpecificTask
set CMD=%1
set SERVERURL=%2
set REPOSITORYID=%3
set DIRECTORYITEMID=%4
set LOGIN=%5
set PASSWORD=%6
set GROUP=%7
set PRIORITY=%8
set OVERRIDEN_OPTIONS=%9


java -cp  %LIBS% %CLASS% CMD SERVERURL REPOSITORYID DIRECTORYITEMID LOGIN PASSWORD GROUP PRIORITY OVERRIDEN_OPTIONS