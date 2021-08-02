FROM jboss/business-central-workbench:7.57.0.Final

COPY ./conf/configure-standalone.sh $JBOSS_HOME
COPY ./conf/standalone-config.cli $JBOSS_HOME

USER root

RUN chmod 755 $JBOSS_HOME/configure-standalone.sh && chown jboss:jboss $JBOSS_HOME/configure-standalone.sh

USER jboss

RUN rm $JBOSS_HOME/standalone/deployments/business-central.war.* \
	&& mkdir -p /opt/jboss/repositories/git \
        && mkdir -p /opt/jboss/repositories/maven \
        && mkdir -p /opt/jboss/data \
        && mkdir -p /opt/jboss/git-hooks \
        && touch $JBOSS_HOME/standalone/deployments/business-central.war.dodeploy \
        && $JBOSS_HOME/bin/add-user.sh -a -u controllerUser -p password@1 -g kie-server,rest-all \
        && $JBOSS_HOME/bin/add-user.sh -a -u jbpmAdmin -p password@1 -g admin,kie-server,rest-all \
	&& $JBOSS_HOME/bin/add-user.sh admin password@1 \
	&& $JBOSS_HOME/configure-standalone.sh \
	&& rm -rf $JBOSS_HOME/standalone/tmp \
	&& rm -rf $JBOSS_HOME/standalone/data

COPY ./conf/post-commit /opt/jboss/git-hooks
COPY ./conf/settings.xml /opt/jboss/repositories/maven

USER root

RUN chmod 744 /opt/jboss/git-hooks/post-commit \
	&& chown -R jboss:jboss /opt/jboss

USER jboss

ENV JAVA_OPTS="$JAVA_OPTS -Xms1500m -Xmx1500m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=768m -Djava.net.preferIPv4Stack=true -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n"
####### RUNNING JBPM-WB ############
#WORKDIR $JBOSS_HOME/bin/
#CMD ["./start_jbpm-wb.sh"]
