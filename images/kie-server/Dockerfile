FROM jboss/kie-server:latest

COPY ./conf/configure-standalone.sh $JBOSS_HOME
COPY ./conf/standalone-config.cli $JBOSS_HOME

RUN mv $JBOSS_HOME/standalone/deployments/kie-server.war.dodeploy $JBOSS_HOME/standalone/deployments/kie-server.war.undeploy \
        && mkdir -p /opt/jboss/repositories/kie\
        && mkdir -p /opt/jboss/data

USER root

RUN chmod 755 $JBOSS_HOME/configure-standalone.sh \
        && chown jboss:jboss $JBOSS_HOME/configure-standalone.sh

USER jboss

RUN touch $JBOSS_HOME/standalone/deployments/kie-server.war.dodeploy \
        && $JBOSS_HOME/bin/add-user.sh -a -u controllerUser -p password@1 -g kie-server,rest-all \
        && $JBOSS_HOME/bin/add-user.sh -a -u jbpmAdmin -p password@1 -g admin,kie-server,rest-all \
	&& $JBOSS_HOME/bin/add-user.sh admin password@1 \
	&& $JBOSS_HOME/configure-standalone.sh standalone standalone-full.xml \ 
	&& cat $JBOSS_HOME/standalone/configuration/standalone-full.xml \
	&& rm -rf $JBOSS_HOME/standalone/tmp \
	&& rm -rf $JBOSS_HOME/standalone/data \
	&& curl https://repository.jboss.org/org/jbpm/jbpm-event-emitters-elasticsearch/7.53.0.Final/jbpm-event-emitters-elasticsearch-7.53.0.Final.jar -o $JBOSS_HOME/standalone/deployments/kie-server.war/WEB-INF/lib/jbpm-event-emitters-elasticsearch-7.53.0.Final.jar

COPY ./libs/ether-bpmaas-designer-kie-container-lifecycle-listener-0.0.1.jar $JBOSS_HOME/standalone/deployments/kie-server.war/WEB-INF/lib
COPY ./conf/settings.xml /opt/jboss/repositories/kie

ENV JAVA_OPTS="$JAVA_OPTS -Xms1280m -Xmx1280m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=512m -Djava.net.preferIPv4Stack=true -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n"

####### RUNNING JBPM-WB ############
WORKDIR $JBOSS_HOME/bin/
CMD ["./start_kie-wb.sh"]
