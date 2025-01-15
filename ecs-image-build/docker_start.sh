#!/bin/bash

# Start script for order-notification-sender

PORT=8080
exec java -jar -Dserver.port="${PORT}" "order-notification-sender.jar"
