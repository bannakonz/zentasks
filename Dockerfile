FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

# ✅ Skip tests during image build
RUN ./gradlew build -x test

CMD ["java", "-jar", "build/libs/zentasks-0.0.1-SNAPSHOT.jar"]
