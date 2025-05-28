package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSendFailedEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.ApiClient;

/**
 * Handles an order notification by enriching it with data fetched from the orders API.
 */
@Service
public class OrderNotificationSenderService implements ApplicationEventPublisherAware {

    private final OrderNotificationEnrichable orderEnricher;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;
    private final ApiClient apiClient;
    private final String chsKafkaUrl;

    public OrderNotificationSenderService(@Value("${chs.kafka.api.endpoint}") String chsKafkaUrl,
            OrderNotificationEnrichable orderEnricher,
            LoggingUtils loggingUtils,
            ApiClient apiClient) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
        this.apiClient = apiClient;
        this.chsKafkaUrl = chsKafkaUrl;
    }

    /**
     * Handles an order notification by enriching it with data fetched from the orders API. If an error occurs when
     * enriching the notification then a failure event will be published.
     *
     * @param event The order that is being processed.
     */
    @EventListener
    public void handleEvent(final SendOrderNotificationEvent event) {
        Logger logger = loggingUtils.getLogger();
        logger.trace(format("handleEvent(%s) method called.", event));

        Map<String, Object> loggerArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, event.getOrderURI());
        logger.debug("Preparing to enrich order; using order enricher...", loggerArgs);

        try {
            var emailSend = orderEnricher.enrich(event.getOrderURI());
            logger.debug(format("Successfully enriched order; preparing to build email payload from model: %s",
                    convertToString(emailSend)));

            var sendEmail = new SendEmail();
            sendEmail.setAppId(emailSend.getAppId());
            sendEmail.setMessageId(emailSend.getMessageId());
            sendEmail.setMessageType(emailSend.getMessageType());
            sendEmail.setJsonData(emailSend.getData());
            sendEmail.setEmailAddress(emailSend.getEmailAddress());

            logger.debug(format("Email Payload: %s", sendEmail));

            var internalApiClient = apiClient.getInternalApiClient();
            internalApiClient.setBasePath(chsKafkaUrl);

            var sendEmailHandler = internalApiClient.sendEmailHandler();
            var sendEmailPost = sendEmailHandler.postSendEmail("/send-email", sendEmail);

            ApiResponse<Void> response = sendEmailPost.execute();

            logger.info(String.format("Posted '%s' email to CHS Kafka API (AppId: %s): (Response %d)",
                    sendEmail.getMessageType(), sendEmail.getAppId(), response.getStatusCode()));

        } catch (RetryableErrorException e) {
            logger.error("Failed to enrich order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(event));

        } catch (ApiErrorResponseException e) {
            logger.error("Failed to send email for enriched order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));
        }
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {
        this.applicationEventPublisher = publisher;
    }

    private String convertToString(final EmailSend emailSend) {
        try {
            new ObjectMapper().writeValueAsString(emailSend);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
