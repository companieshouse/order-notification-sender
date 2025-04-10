package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSendFailedEvent;
import uk.gov.companieshouse.ordernotification.emailsender.SendEmailEvent;
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
    private ApiClient apiClient;

    public OrderNotificationSenderService(OrderNotificationEnrichable orderEnricher, LoggingUtils loggingUtils, ApiClient apiClient) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
        this.apiClient = apiClient;
    }

    /**
     * Handles an order notification by enriching it with data fetched from the orders API. If an error occurs when
     * enriching the notification then a failure event will be published.
     *
     * @param sendOrderNotificationEvent The order that is being processed.
     */
    @EventListener
    public void handleEvent(SendOrderNotificationEvent sendOrderNotificationEvent) {
        Map<String, Object> loggerArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, sendOrderNotificationEvent.getOrderURI());
        try {
            InternalApiClient internalApiClient = apiClient.getInternalApiClient();
            EmailSend emailSend = orderEnricher.enrich(sendOrderNotificationEvent.getOrderURI());
            loggingUtils.getLogger().debug("Successfully enriched order; notifying email sender", loggerArgs);

            SendEmail sendEmail = new SendEmail();
            sendEmail.setAppId(emailSend.getAppId());
            sendEmail.setMessageId(emailSend.getMessageId());
            sendEmail.setMessageType(emailSend.getMessageType());
            sendEmail.setJsonData(emailSend.getData());
            sendEmail.setEmailAddress(emailSend.getEmailAddress());

            PrivateSendEmailHandler sendEmailHandler = internalApiClient.sendEmailHandler();
            PrivateSendEmailPost sendEmailPost = sendEmailHandler.postSendEmail("/send-email", sendEmail);
            sendEmailPost.execute();
        } catch (RetryableErrorException e) {
            loggingUtils.getLogger().error("Failed to enrich order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(sendOrderNotificationEvent));
        } catch (ApiErrorResponseException e) {
            loggingUtils.getLogger().error("Failed to send email for enriched order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(sendOrderNotificationEvent));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
