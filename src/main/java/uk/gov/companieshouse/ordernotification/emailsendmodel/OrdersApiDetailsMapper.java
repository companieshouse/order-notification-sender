package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.ItemReadyEmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.MessageTypeConfigProvider;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;

/**
 * Map an {@link OrdersApi} object to an {@link EmailSend} object.
 */
@Component
public class OrdersApiDetailsMapper {

    private final DateGenerator dateGenerator;
    private final EmailConfiguration config;
    private final ItemReadyEmailConfiguration itemReadyEmailConfig;
    private final ObjectMapper objectMapper;
    private final OrderNotificationEmailDataBuilderFactory factory;

    public OrdersApiDetailsMapper(DateGenerator dateGenerator,
        EmailConfiguration config,
        ItemReadyEmailConfiguration itemReadyEmailConfig,
        ObjectMapper objectMapper,
        OrderNotificationEmailDataBuilderFactory factory) {
        this.dateGenerator = dateGenerator;
        this.config = config;
        this.itemReadyEmailConfig = itemReadyEmailConfig;
        this.objectMapper = objectMapper;
        this.factory = factory;
    }

    /**
     * Delegates mapping of ordersApiWrappable to OrderNotificationEmailData to the director and
     * then builds an EmailSend object from the emailData and email configuration properties.
     *
     * @param ordersApiWrappable A wrapper interface for an OrdersApi object
     * @return EmailSend
     */
    public EmailSend mapToEmailSend(OrdersApiWrappable ordersApiWrappable) {
        OrderNotificationDataConvertable converter = factory.newConverter();
        mapDirector(ordersApiWrappable, converter);
        try {
            return buildEmail(converter, config);
        } catch (JsonProcessingException e) {
            throw new MappingException(
                "Failed to map order: " + ordersApiWrappable.getOrdersApi().getReference(), e);
        }
    }

    /**
     * Delegates mapping of ordersApiWrappable to OrderNotificationEmailData to the director and
     * then builds an EmailSend object from the emailData, the email configuration properties and
     * the incoming item ready notification.
     *
     * @param ordersApiWrappable    A wrapper interface for an OrdersApi object
     * @param itemReadyNotification the incoming item ready notification
     * @return EmailSend
     */
    public EmailSend mapToEmailSend(final OrdersApiWrappable ordersApiWrappable, final
    ItemGroupProcessedSend itemReadyNotification) {
        OrderNotificationDataConvertable converter = factory.newConverter(itemReadyNotification,
            itemReadyEmailConfig);
        mapDirector(ordersApiWrappable, converter);
        try {
            return buildEmail(converter, itemReadyEmailConfig);
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to map order and item ready notification: "
                + ordersApiWrappable.getOrdersApi().getReference() + ", "
                + itemReadyNotification.getItem().getId(), e);
        }
    }

    private EmailSend buildEmail(final OrderNotificationDataConvertable converter,
        final MessageTypeConfigProvider messageTypeConfigProvider)
        throws JsonProcessingException {
        EmailSend emailSend = new EmailSend();
        emailSend.setEmailAddress(config.getSenderAddress());
        emailSend.setData(objectMapper.writeValueAsString(converter.getEmailData()));
        emailSend.setMessageId(messageTypeConfigProvider.getMessageId());
        emailSend.setAppId(config.getApplicationId());
        emailSend.setMessageType(messageTypeConfigProvider.getMessageType());
        emailSend.setCreatedAt(dateGenerator.generate()
            .format(DateTimeFormatter.ofPattern(config.getDateFormat())));
        return emailSend;
    }

    private void mapDirector(final OrdersApiWrappable ordersApiWrappable,
        final OrderNotificationDataConvertable converter) {
        SummaryEmailDataDirector director = factory.newDirector(converter);
        director.map(ordersApiWrappable.getOrdersApi());
    }
}
