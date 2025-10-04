# Use official OpenJDK image
FROM openjdk:25-ea-4-jdk-oraclelinux9

# Set working directory
WORKDIR /app

# Copy Maven build output
COPY target/user-app-0.0.1-SNAPSHOT.jar user-app.jar

# Expose port
EXPOSE 8081

# Run application
ENTRYPOINT ["java", "-jar", "user-app.jar"]

#FROM openjdk:25-ea-4-jdk-oraclelinux9
#
#WORKDIR /app
#
#COPY target/user-app-0.0.1-SNAPSHOT.jar user-app.jar
#
#ENV Instance="Main Instance"
#
#EXPOSE 8081
#
#ENTRYPOINT ["java", "-jar", "/app/app.jar"]
