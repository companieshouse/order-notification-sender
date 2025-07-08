#!/bin/bash

# Start script for order-notification-sender

PORT=8080
exec java -jar -Dserver.port="${PORT}" -XX:MaxRAMPercentage=80 "order-notification-sender.jar"
