FROM registry.redhat.io/ubi8/openjdk-11:1.14-12
WORKDIR /opt/app
ARG JAR_FILE=target/Muis-Fuse-xQuery-transformation-1.0.0.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8086/tcp
ENTRYPOINT ["java","-jar","app.jar"]