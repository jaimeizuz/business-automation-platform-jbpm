FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/h2/data && cd /opt/h2/ \
	&& wget https://repo1.maven.org/maven2/com/h2database/h2/1.4.197/h2-1.4.197.jar

COPY ./config/.h2.server.properties /opt/h2/data

WORKDIR /opt/h2

CMD ["java", "-Dh2.bindAddress=0.0.0.0", "-jar", "h2-1.4.197.jar", "-webAllowOthers", "-tcpAllowOthers", "-properties", "/opt/h2/data"]
