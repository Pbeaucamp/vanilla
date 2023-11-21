#!/bin/sh
exec ps -u root -o pid,command | grep 'java -jar start.jar' | awk '{print $1}' | xargs kill -9
