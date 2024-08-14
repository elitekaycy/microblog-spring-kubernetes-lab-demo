FROM openjdk:21

VOLUME /tmp

COPY target/gatewayservice.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]

