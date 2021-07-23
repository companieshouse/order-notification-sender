FROM alpine:3.14.0

ENV STARTUP_PATH=/opt/order-notification-sender/order-notification-sender.jar

RUN apk --no-cache add \
    bash \
    openjdk8 \
    curl

COPY order-notification-sender.jar $STARTUP_PATH
COPY start.sh /usr/local/bin/

RUN chmod 555 /usr/local/bin/start.sh

HEALTHCHECK --interval=1m --timeout=10s --retries=3 --start-period=1m CMD curl --fail http://localhost:8080/healthcheck || exit 1

CMD ["start.sh"]
