FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/myapp.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]