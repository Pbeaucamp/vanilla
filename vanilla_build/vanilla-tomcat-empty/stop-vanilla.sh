#!/bin/sh

if [ $# -eq 0 ]
 then
	exec ps -u vanilla -o pid,command | grep 'org\.apache\.catalina\.startup\.Bootstrap' | awk '{print $1}' | xargs kill -9
else
	cat $1  | xargs kill -9 
fi