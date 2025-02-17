# ---------------------
# --- Builder Stage ---
# ---------------------
FROM bellsoft/liberica-openjdk-debian:21.0.4-cds AS builder

# Install dependencies and gradle 8.12
RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*
RUN wget -q https://services.gradle.org/distributions/gradle-8.12-bin.zip -O gradle.zip \
    && unzip gradle.zip -d /opt/gradle \
    && rm gradle.zip
ENV GRADLE_HOME=/opt/gradle/gradle-8.12
ENV PATH=$GRADLE_HOME/bin:$PATH

# Copy project files
WORKDIR /app
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

# Gradle wrapper check
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# ---------------------
# --- Runtime Stage ---
# ---------------------
FROM bellsoft/liberica-openjdk-debian:21.0.4-cds

# Install tools (for debugging)
RUN apt-get update && apt-get install -y \
    curl vim net-tools htop zip unzip \
    && rm -rf /var/lib/apt/lists/*

# Copy built jar from the builder stage
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# Create config directory for dynamic config
RUN mkdir -p /app/config

# Copy entrypoint script that generates config from environment variables
COPY .github/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
