FROM openjdk:8-jre-alpine

ENV KIE_VERSION=7.59.0.Final

RUN mkdir -p /opt/jboss/pim \
	&& wget https://repository.jboss.org/nexus/content/repositories/public-jboss/org/drools/process-migration-service/${KIE_VERSION}/process-migration-service-${KIE_VERSION}-thorntail.jar -P /opt/jboss/pim \
	&& mv /opt/jboss/pim/process-migration-service-${KIE_VERSION}-thorntail.jar /opt/jboss/pim/process-migration-service-thorntail.jar \
	&& ls -lart /opt/jboss/pim

COPY ./conf/project-cloud.yml /opt/jboss/pim
COPY ./conf/application-users.properties /opt/jboss/pim
COPY ./conf/application-roles.properties /opt/jboss/pim

WORKDIR /opt/jboss/pim

CMD ["java","-Djava.net.preferIPv4Stack=true","-Djava.net.preferIPv4Addresses=true","-jar","/opt/jboss/pim/process-migration-service-thorntail.jar","-Dswarm.network.socket-binding-groups.standard-sockets.port-offset=10","-s","/opt/jboss/pim/project-cloud.yml"]
