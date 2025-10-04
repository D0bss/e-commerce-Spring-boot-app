#FROM openjdk:25-ea-4-jdk-oraclelinux9
#
#WORKDIR /app
#
#COPY target/ target/
#COPY target/AdminService-0.0.1-SNAPSHOT.jar target/adminApp.jar
#
#EXPOSE 8082
#
#ENTRYPOINT ["java", "-jar", "target/adminApp.jar"]

FROM openjdk:25-ea-jdk-oraclelinux9

WORKDIR /app

COPY target/ target/
COPY target/ProductService-0.0.1-SNAPSHOT.jar target/productApp.jar


EXPOSE 8082

ENTRYPOINT ["java", "-jar", "target/productApp.jar"]