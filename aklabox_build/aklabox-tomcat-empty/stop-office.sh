#!/bin/sh
exec ps -u root -o pid,command | grep '/usr/lib/libreoffice/program/soffice.bin' | awk '{print $1}' | xargs kill -9
