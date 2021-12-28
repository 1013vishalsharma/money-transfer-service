FROM adoptopenjdk/openjdk11:latest
LABEL VISHAL SHARMA
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]