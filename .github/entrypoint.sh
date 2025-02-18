#!/bin/bash
set -e

CONFIG_FILE="/app/config/application.properties"
PROD_CONFIG_FILE="/app/config/application-production.properties"
OAUTH_CONFIG_FILE="/app/config/application-oauth.yml"

if [ ! -f "$CONFIG_FILE" ]; then
  echo "spring.profiles.active=production" > "$CONFIG_FILE"
elif grep -q "^spring.profiles.active" "$CONFIG_FILE"; then
  sed -i 's/^spring.profiles.active=.*/spring.profiles.active=production/' "$CONFIG_FILE"
else
  echo "spring.profiles.active=production" >> "$CONFIG_FILE"
fi

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

if [ -n "$APPLICATION_OAUTH_CONFIG" ]; then
  echo "$APPLICATION_OAUTH_CONFIG" | base64 --decode > "$OAUTH_CONFIG_FILE"
  echo "Decoded OAuth config file from secret."
else
  echo "APPLICATION_OAUTH_CONFIG not provided, skipping OAuth config generation."
fi


exec java -jar app.jar --spring.config.additional-location=file:/app/config/
