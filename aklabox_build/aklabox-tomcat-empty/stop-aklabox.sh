#!/bin/sh
exec ps -u root -o pid,command | grep 'org\.apache\.catalina\.startup\.Bootstrap' | awk '{print $1}' | xargs kill -9
