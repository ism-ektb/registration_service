FROM amazoncorretto:17
COPY registration/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]