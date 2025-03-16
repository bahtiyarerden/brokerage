FROM eclipse-temurin:21 AS builder
WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app

COPY --from=builder /app/target/*.jar brokerage-app.jar

ENTRYPOINT ["java", "-jar", "brokerage-app.jar"]
