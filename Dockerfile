FROM openjdk:17-jdk-slim
WORKDIR application
COPY target/spring-book-app.jar ./
ENTRYPOINT ["java", "-jar","spring-book-app.jar"]