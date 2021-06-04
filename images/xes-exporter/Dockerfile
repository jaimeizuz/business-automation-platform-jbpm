FROM jboss/business-central-workbench-showcase:latest

USER root

COPY ./configure-standalone.sh $JBOSS_HOME
COPY ./standalone-config.cli $JBOSS_HOME

RUN rm $JBOSS_HOME/standalone/deployments/business-central.war.* $JBOSS_HOME/standalone/deployments/kie-server.war.* \
        && mv $JBOSS_HOME/standalone/deployments/kie-server.war $JBOSS_HOME/standalone/ \
        && mkdir -p $JBOSS_HOME/standalone/deployments/kie-server.war \
        && unzip $JBOSS_HOME/standalone/kie-server.war -d $JBOSS_HOME/standalone/deployments/kie-server.war \
        && rm -rf $JBOSS_HOME/standalone/*.war \
	&& mkdir -p /opt/jboss/repositories/git \
        && mkdir -p /opt/jboss/repositories/maven \
        && mkdir -p /opt/jboss/repositories/kie\
        && mkdir -p /opt/jboss/data \
        && mkdir -p /opt/jboss/git-hooks \
        && mkdir -p /opt/jboss/mock-server \
	&& mkdir -p /opt/jboss/pim \
	&& mkdir $JBOSS_HOME/standalone/configuration/users_groups_data \
#	&& mv $JBOSS_HOME/standalone/configuration/application-* $JBOSS_HOME/standalone/configuration/users_groups_data \
        && yum install -y epel-release && yum -y install git inotify-tools && yum -y clean all \
        && chmod 755 $JBOSS_HOME/configure-standalone.sh \
        && chown jboss:jboss $JBOSS_HOME/configure-standalone.sh \
        && touch $JBOSS_HOME/standalone/deployments/business-central.war.dodeploy \
        && touch $JBOSS_HOME/standalone/deployments/kie-server.war.dodeploy \
        && touch $JBOSS_HOME/standalone/deployments/jbpm-casemgmt.war.dodeploy \
        && $JBOSS_HOME/bin/add-user.sh -a -u controllerUser -p password@1 -g kie-server,rest-all \
        && $JBOSS_HOME/bin/add-user.sh -a -u jbpmAdmin -p password@1 -g admin,kie-server,rest-all \
	&& $JBOSS_HOME/bin/add-user.sh admin password@1 \
	&& $JBOSS_HOME/configure-standalone.sh \
	&& curl https://repository.jboss.org/nexus/content/repositories/public-jboss/org/drools/process-migration-service/${KIE_VERSION}/process-migration-service-${KIE_VERSION}-thorntail.jar --output /opt/jboss/pim/process-migration-service-${KIE_VERSION}-thorntail.jar \
#	&& curl https://repository.jboss.org/nexus/content/repositories/public/org/dashbuilder/dashbuilder-runtime/${KIE_VERSION}/dashbuilder-runtime-${KIE_VERSION}.war --output  $JBOSS_HOME/standalone/deployments/dashbuilder-runtime-${KIE_VERSION}.war \
#	&& touch $JBOSS_HOME/standalone/deployments/dashbuilder-runtime-${KIE_VERSION}.war.dodeploy \
	&& rm -rf $JBOSS_HOME/standalone/tmp \
	&& rm -rf $JBOSS_HOME/standalone/data \
	&& sed '/bash/a java -Dh2.bindAddress=0.0.0.0 -jar /opt/jboss/wildfly/modules/system/layers/base/com/h2database/h2/main/h2-1.4.197.jar -webAllowOthers -tcpAllowOthers -properties /opt/jboss/data &' -i start_jbpm-wb.sh \
#       && sed '/bash/a /opt/jboss/mock-server/start-mock-server.sh' -i start_jbpm-wb.sh \
#       && sed '/bash/a /opt/jboss/mock-server/watch-mock-services-definitions.sh &' -i start_jbpm-wb.sh \
        && sed '/bash/a echo \"Initializing h2 server...\"' -i start_jbpm-wb.sh \
	&& sed '/bash/a java -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -jar /opt/jboss/pim/process-migration-service-${KIE_VERSION}-thorntail.jar -Dswarm.network.socket-binding-groups.standard-sockets.port-offset=10 -s /opt/jboss/pim/project-cloud.yml &' -i start_jbpm-wb.sh

COPY ./mock-server/ /opt/jboss/mock-server
COPY ./post-commit /opt/jboss/git-hooks
COPY ./pim/project-cloud.yml /opt/jboss/pim
COPY ./pim/application-users.properties /opt/jboss/pim
COPY ./pim/application-roles.properties /opt/jboss/pim
COPY ./libs/ether-bpmaas-designer-kie-container-lifecycle-listener-0.0.1.jar $JBOSS_HOME/standalone/deployments/kie-server.war/WEB-INF/lib


# COPY ./libs/jbpm-event-emitters-elasticsearch-7.33.0.Final.jar $JBOSS_HOME/standalone/deployments/kie-server.war/WEB-INF/lib

RUN chmod 744 /opt/jboss/git-hooks/post-commit \
	&& chown -R jboss:jboss /opt/jboss

ENV JAVA_OPTS="$JAVA_OPTS -Xms1500m -Xmx2560m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=768m -Djava.net.preferIPv4Stack=true -agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=n"

USER jboss

####### RUNNING JBPM-WB ############
WORKDIR $JBOSS_HOME/bin/
CMD ["./start_jbpm-wb.sh"]
