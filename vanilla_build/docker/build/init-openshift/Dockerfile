# syntax=docker/dockerfile:1
# Image officiel DockerHub
FROM eeacms/rsync:2.3

ENV DATA /init-data
ENV HOME /home

RUN mkdir -p $DATA/vanilla-conf && \
    mkdir -p $DATA/db && \
    mkdir -p $HOME/data

# Add (copy) the configuration files
ADD /docker/build/compilation/vanilla/vanilla-conf/customsettings.properties $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/log.xml $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/logback.xml $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/repositoryone.properties $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/update_manager.properties $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/vanilla.properties $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/vanilla_definition.xml $DATA/vanilla-conf
ADD /docker/build/compilation/vanilla/vanilla-conf/test.xml $DATA/vanilla-conf

# Add (copy and untar) the vanilla files
ADD /docker/build/compilation/vanilla_files.tar.gz $DATA

# Add (copy) the DB files
ADD /docker/vanilla-docker-deployement/init/init_user.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/repository.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/sampledata.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/vanilla_hub.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/vanilla_kpi.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/vanilla.sql $DATA/db
ADD /docker/vanilla-docker-deployement/init/vanilladata.sql $DATA/db

RUN chgrp -R 0 $DATA && \
    chmod -R g=u $DATA && \
    chgrp -R 0 $HOME && \
    chmod -R g=u $HOME