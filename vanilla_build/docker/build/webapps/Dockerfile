	# syntax=docker/dockerfile:1
# Image officiel DockerHub
FROM jetty:9.4.44-jdk8

USER root

ENV VANILLA_HOME /var/lib/jetty/vanilla
ENV VANILLA_WEBAPPS /var/lib/jetty/webapps

# Add this module because if not openshift redirect from https to http and webapps cannot open in iframe anymore
RUN echo "--module=http-forwarded" > /var/lib/jetty/start.d/http-forwarded.ini

# Create folder vanilla
RUN mkdir -p $VANILLA_HOME
RUN mkdir -p $VANILLA_HOME/vanilla-conf

# OpenShift recommendation - Set right for users in the root group to access - Allow to use other user than root
RUN chgrp -R 0 /usr/local/jetty && \
    chmod -R g=u /usr/local/jetty && \
    chgrp -R 0 /var/lib/jetty && \
    chmod -R g=u /var/lib/jetty && \
    chgrp -R 0 /tmp && \
    chmod -R g=u /tmp

# Copy Vanilla licenses
COPY --chown=jetty:root /docker/build/compilation/licenses/Vanilla_Public_License1.1.pdf $JETTY_BASE
COPY --chown=jetty:root /docker/build/compilation/licenses/Vanilla_Public_License1.1.txt $JETTY_BASE

# Add (copy and untar) the folder webapps with webapps
#ADD --chown=jetty:root /docker/build/compilation/UpdateManager.tar.gz $VANILLA_WEBAPPS # Not using this webapp for now
ADD --chown=jetty:root /docker/build/compilation/VanillaKpiMap.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaKpiLoader.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaKpiDesigner.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaKpiUser.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaAnalysis.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaAir.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaDataPreparation.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaDashboard.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaMetadata.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaReport.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaArchitect.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/VanillaHub.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/vanilla.tar.gz $VANILLA_WEBAPPS
ADD --chown=jetty:root /docker/build/compilation/vanilla_files.tar.gz $VANILLA_WEBAPPS

# Add (copy and untar) the folder vanilla with resources
ADD --chown=jetty:root /docker/build/compilation/vanilla/resources.tar.gz $VANILLA_HOME

# Add (copy and untar) the configuration files
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/customsettings.properties $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/log.xml $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/logback.xml $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/repositoryone.properties $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/update_manager.properties $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/vanilla.properties $VANILLA_HOME/vanilla-conf
ADD --chown=jetty:root /docker/build/compilation/vanilla/vanilla-conf/vanilla_definition.xml $VANILLA_HOME/vanilla-conf

# Add simlink to vanilla_files for properties
RUN ln -s $VANILLA_WEBAPPS/vanilla_files /vanilla_files

# Changing default session time
RUN sed -i 's/<session-timeout>30/<session-timeout>1440/g' /usr/local/jetty/etc/webdefault.xml

RUN chgrp -R 0 $VANILLA_WEBAPPS/vanilla_files && \
    chmod -R g=u $VANILLA_WEBAPPS/vanilla_files && \
    chgrp -R 0 $VANILLA_HOME/vanilla-conf && \
    chmod -R g=u $VANILLA_HOME/vanilla-conf

USER 3005
EXPOSE 8080