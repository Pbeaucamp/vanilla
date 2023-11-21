# syntax=docker/dockerfile:1
# Image officiel DockerHub
FROM openjdk:8-jdk

# Install packages
RUN rm /bin/sh && ln -s /bin/bash /bin/sh && \
    apt-get update && apt-get install --no-install-recommends -y \
    curl \
    gdal-bin
    
ENV WORK_DIR /usr/local/vanilla
ENV VANILLA_HOME /usr/local/vanilla/vanilla
ENV TMPDIR /tmp/jetty

# Create folder vanilla
RUN mkdir -p $WORK_DIR/data
RUN mkdir -p $WORK_DIR/logs
RUN mkdir -p $WORK_DIR/workspace
RUN mkdir -p $VANILLA_HOME
RUN mkdir -p $VANILLA_HOME/vanilla-conf
RUN mkdir -p "$TMPDIR" ;

# RUN groupadd -r jetty && useradd -r -g jetty jetty
# RUN chown -R jetty:jetty "$WORK_DIR" "$VANILLA_HOME" "$TMPDIR"

# Copy Vanilla licenses
#COPY /docker/build/compilation/licenses/Vanilla_Public_License1.1.pdf $VANILLA_HOME
#COPY /docker/build/compilation/licenses/Vanilla_Public_License1.1.txt $VANILLA_HOME
COPY --chown=root:root /docker/build/compilation/licenses/Vanilla_Public_License1.1.pdf $VANILLA_HOME
COPY --chown=root:root /docker/build/compilation/licenses/Vanilla_Public_License1.1.txt $VANILLA_HOME

# OpenShift recommendation - Set right for users in the root group to access - Allow to use other user than root
RUN chmod -R g=u $WORK_DIR && \
    chmod -R g=u /tmp && \
    chgrp -R 0 $WORK_DIR/data && \
    chmod -R g=u $WORK_DIR/data && \
    chgrp -R 0 $WORK_DIR/logs && \
    chmod -R g=u $WORK_DIR/logs && \
    chgrp -R 0 $WORK_DIR/workspace && \
    chmod -R g=u $WORK_DIR/workspace

# Add (copy and untar) the folder vanilla with resources
#ADD /docker/build/compilation/vanilla/resources.tar.gz $VANILLA_HOME
ADD --chown=root:root /docker/build/compilation/vanilla/resources.tar.gz $VANILLA_HOME

# Add (copy and untar) the vanilla runtime
#ADD /docker/build/compilation/VanillaRuntime.tar.gz $VANILLA_HOME
ADD --chown=root:root /docker/build/compilation/VanillaRuntime.tar.gz $VANILLA_HOME
RUN	chmod -R g=u $VANILLA_HOME

COPY /docker/build/compilation/vanilla/bpm.vanilla.api-0.0.1.jar $VANILLA_HOME/VanillaRuntime

# Add simlink to vanilla_files for properties
RUN ln -s $WORK_DIR/webapps/vanilla_files /vanilla_files

COPY /docker/build/compilation/patches ${WORK_DIR}/patches

RUN if [ -z "$(ls -A $WORK_DIR/patches)" ]; then \
		echo "No patch to apply"; \
	else \
		for d in $WORK_DIR/patches/*; do \
			echo "$0: Applying patch $d to $VANILLA_HOME/VanillaRuntime"; cp "$d" $VANILLA_HOME/VanillaRuntime; \
	    done \
	fi

WORKDIR $WORK_DIR
COPY /docker/build/runtime/docker-entrypoint.sh /

USER 3005
EXPOSE 7171
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["java", "-jar", "vanilla/VanillaRuntime/org.eclipse.osgi_3.13.200.v20181130-2106.jar", "-consoleLog -console"]
