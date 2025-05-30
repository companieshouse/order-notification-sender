package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import static java.lang.String.format;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSendFailedEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

/**
 * Handles an order notification by enriching it with data fetched from the orders API.
 */
@Service
public class OrderNotificationSenderService implements ApplicationEventPublisherAware {

    private final OrderNotificationEnrichable orderEnricher;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;
    private final Supplier<InternalApiClient> internalApiClientSupplier;

    public OrderNotificationSenderService(OrderNotificationEnrichable orderEnricher,
            LoggingUtils loggingUtils,
            Supplier<InternalApiClient> internalApiClientSupplier) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
        this.internalApiClientSupplier = internalApiClientSupplier;
    }

    /**
     * Handles an order notification by enriching it with data fetched from the orders API. If an error occurs when
     * enriching the notification then a failure event will be published.
     *
     * @param event The order that is being processed.
     */
    @EventListener
    public void handleEvent(final SendOrderNotificationEvent event) {
        loggingUtils.getLogger().trace(format("handleEvent(%s) method called.", event));

        Map<String, Object> loggerArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, event.getOrderURI());

        try {
            loggingUtils.getLogger().debug("Preparing to enrich order; using order enricher...", loggerArgs);
            var emailSend = orderEnricher.enrich(event.getOrderURI());

            var sendEmail = new SendEmail();
            sendEmail.setAppId(emailSend.getAppId());
            sendEmail.setMessageId(emailSend.getMessageId());
            sendEmail.setMessageType(emailSend.getMessageType());
            sendEmail.setJsonData(emailSend.getData());
            sendEmail.setEmailAddress(emailSend.getEmailAddress());

            var requestId = getRequestId().orElse(UUID.randomUUID().toString());

            var apiClient = internalApiClientSupplier.get();
            apiClient.getHttpClient().setRequestId(requestId);

            var sendEmailHandler = apiClient.sendEmailHandler();
            var sendEmailPost = sendEmailHandler.postSendEmail("/send-email", sendEmail);

            loggingUtils.logAsJson("SendEmail", sendEmail);
            ApiResponse<Void> response = sendEmailPost.execute();

            loggingUtils.getLogger().info(format("Posted '%s' type email to CHS Kafka API (AppId: %s): (Response %d)",
                    sendEmail.getMessageType(), sendEmail.getAppId(), response.getStatusCode()));

        } catch (RetryableErrorException e) {
            loggingUtils.getLogger().error("Failed to enrich order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(event));

        } catch (ApiErrorResponseException e) {
            loggingUtils.getLogger().error("Failed to send email for enriched order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));

        } catch (Exception e) {
            loggingUtils.getLogger().error("Failed to send email for enriched order; an unknown error occurred", e, loggerArgs);
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));
        }
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {
        loggingUtils.getLogger().trace(format("setApplicationEventPublisher(%s) method called.", publisher.getClass().getSimpleName()));
        this.applicationEventPublisher = publisher;
    }

    private Optional<String> getRequestId() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.getRequest().getHeader("x-request-id"));
    }

}
