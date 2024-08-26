FROM openjdk:21

VOLUME /tmp

COPY target/post-service.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]

