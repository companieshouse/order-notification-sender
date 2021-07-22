FROM alpine:3.14.0

ENV STARTUP_PATH=/opt/order-notification-sender/order-notification-sender.jar

RUN apk --no-cache add \
    bash \
    openjdk8 \
    curl

COPY order-notification-sender.jar $STARTUP_PATH
COPY start.sh /usr/local/bin/

RUN chmod 555 /usr/local/bin/start.sh

CMD ["start.sh"]
