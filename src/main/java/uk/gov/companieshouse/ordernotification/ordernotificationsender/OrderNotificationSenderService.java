package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import static java.lang.String.format;

import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
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
    private final Supplier<InternalApiClient> apiClient;

    public OrderNotificationSenderService(OrderNotificationEnrichable orderEnricher,
            LoggingUtils loggingUtils,
            Supplier<InternalApiClient> apiClient) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
        this.apiClient = apiClient;
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

            var sendEmail = new SendEmail();
            sendEmail.setAppId(emailSend.getAppId());
            sendEmail.setMessageId(emailSend.getMessageId());
            sendEmail.setMessageType(emailSend.getMessageType());
            sendEmail.setJsonData(emailSend.getData());
            sendEmail.setEmailAddress(emailSend.getEmailAddress());

            loggingUtils.logAsJson("SendEmail", sendEmail);

            var sendEmailHandler = apiClient.get().sendEmailHandler();
            var sendEmailPost = sendEmailHandler.postSendEmail("/send-email", sendEmail);

            ApiResponse<Void> response = sendEmailPost.execute();

            logger.info(format("Posted '%s' email to CHS Kafka API (AppId: %s): (Response %d)",
                    sendEmail.getMessageType(), sendEmail.getAppId(), response.getStatusCode()));

        } catch (RetryableErrorException e) {
            logger.debug(format("RetryableErrorException has been raised: %s", e.getMessage()));

            logger.error("Failed to enrich order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(event));

        } catch (ApiErrorResponseException e) {
            logger.debug(format("ApiErrorResponseException has been raised: %s", e.getMessage()));

            logger.error("Failed to send email for enriched order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));

        } catch(Exception e) {
            logger.error("Unexpected error occurred while processing order notification: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {
        loggingUtils.getLogger().trace(format("setApplicationEventPublisher(%s) method called.", publisher.getClass().getSimpleName()));
        this.applicationEventPublisher = publisher;
    }

}
