#!/bin/bash
set -e

CONFIG_FILE="/app/config/application.properties"
PROD_CONFIG_FILE="/app/config/application-production.properties"

# Copy the production config file if it doesn't exist
if [ ! -f "$CONFIG_FILE" ]; then
  echo "spring.profiles.active=production" > "$CONFIG_FILE"
elif grep -q "^spring.profiles.active" "$CONFIG_FILE"; then
  sed -i 's/^spring.profiles.active=.*/spring.profiles.active=production/' "$CONFIG_FILE"
else
  echo "spring.profiles.active=production" >> "$CONFIG_FILE"
fi

# Generate application-production.properties from env variables if provided
if [ -n "$DB_URL" ] && [ -n "$SERVER_PORT" ] && [ -n "$DB_DIALECT" ] && [ -n "$DB_DRIVER" ] && [ -n "$DB_USERNAME" ] && [ -n "$DB_PASSWORD" ]; then
  cat > "$PROD_CONFIG_FILE" <<EOF
server.port=$SERVER_PORT
spring.datasource.url=$DB_URL
spring.datasource.driver-class-name=$DB_DRIVER
spring.jpa.properties.hibernate.dialect=$DB_DIALECT
spring.datasource.username=$DB_USERNAME
spring.datasource.password=$DB_PASSWORD
serverTimezone=UTC&characterEncoding=UTF-8
EOF
else
  echo "Environment variables not set. Skipping production config generation."
fi

# Start the Spring Boot application with external config location
exec java -jar app.jar --spring.config.additional-location=file:/app/config/
