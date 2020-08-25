FROM openjdk:14-jdk

MAINTAINER Javier Grande Pérez <grande.perez.javier@gmail.com>

WORKDIR "/opt/app"

COPY target/aes-encrypter.jar ./
CMD ["java", "-jar", "aes-encrypter.jar"]

EXPOSE 8080