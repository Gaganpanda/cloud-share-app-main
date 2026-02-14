# --------- Stage 2: Run the application ---------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# 👇 VERY IMPORTANT
ENV SPRING_PROFILES_ACTIVE=default

ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=${MONGODB_URI}", "-jar", "app.jar"]
