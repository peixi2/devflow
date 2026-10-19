FROM node:22-alpine@sha256:0a7108bf6c7bf5de370ffb1a3ed6be93d405b43ff159f681a8d18c0e2bc2e402 AS frontend-builder
WORKDIR /build/frontend

COPY frontend/package*.json ./
RUN --mount=type=cache,target=/root/.npm npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9.9-eclipse-temurin-21-alpine@sha256:5a8b906c4faa11d33f6c74758f67db8eac25441e14b0729f6d50ff78992be58a AS backend-builder
WORKDIR /build

COPY pom.xml ./
COPY src/ ./src/
COPY --from=frontend-builder /build/frontend/dist/ ./src/main/resources/static/
RUN --mount=type=cache,target=/root/.m2 mvn -B -U package -DskipTests

FROM eclipse-temurin:21-jre-alpine@sha256:51ab5e3302e7141ce665ca3ea85e8b5cd648eafbc3c0c90dd79d6537684e4555
WORKDIR /app

RUN addgroup -S devflow && adduser -S devflow -G devflow
COPY --from=backend-builder /build/target/devflow-1.0.0.jar /app/app.jar

USER devflow
EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
