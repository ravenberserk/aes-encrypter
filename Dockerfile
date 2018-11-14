FROM openjdk:8-jre
MAINTAINER Javier Grande Pérez <grande.perez.javier@gmail.com>

ADD ./target/aes-encrypter.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/aes-encrypter.jar"]

EXPOSE 8080