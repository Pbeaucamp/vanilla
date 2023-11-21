#!/bin/sh
cd solr-4.4.0/example/
nohup java -jar start.jar > solr.log 2>&1 &
