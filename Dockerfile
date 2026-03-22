FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

LABEL org.opencontainers.image.source=https://github.com/todagtodag-skhu/Backend
LABEL org.opencontainers.image.description="Todagtodag backend"
LABEL org.opencontainers.image.licenses=UNLICENSED

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
