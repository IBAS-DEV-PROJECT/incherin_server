FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=build/libs/inchelin-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]