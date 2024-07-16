# Use an official Amazon Corretto 17 runtime as a parent image
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build context into the Docker image
COPY target/*.jar escalayt-application.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the entry point to run the application
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "escalayt-application.jar"]
