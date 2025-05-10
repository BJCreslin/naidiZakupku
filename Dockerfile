FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG JAR_FILE=build/libs/myapp.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]