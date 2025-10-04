FROM openjdk:23-slim
ARG JAR_FILE=target/CheckoutService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]
