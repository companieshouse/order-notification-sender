package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;

/**
 * Map an {@link OrdersApi} object to an {@link EmailSend} object.
 */
@Component
public class OrdersApiDetailsMapper {

    private final DateGenerator dateGenerator;
    private final EmailConfiguration config;
    private final ObjectMapper objectMapper;
    private final SummaryEmailDataDirector director;

    public OrdersApiDetailsMapper(DateGenerator dateGenerator,
                                  EmailConfiguration config,
                                  ObjectMapper objectMapper,
                                  SummaryEmailDataDirector director) {
        this.dateGenerator = dateGenerator;
        this.config = config;
        this.objectMapper = objectMapper;
        this.director = director;
    }

    /**
     * Delegates mapping of ordersApiWrappable to OrderNotificationEmailData to the director and
     * then builds an EmailSend object from the emailData and email configuration properties.
     *
     * @param ordersApiWrappable A wrapper interface for an OrdersApi object
     * @return EmailSend
     */
    public EmailSend mapToEmailSend(OrdersApiWrappable ordersApiWrappable) {
        OrderNotificationEmailData emailData = director.map(ordersApiWrappable.getOrdersApi());
        try {
            EmailSend emailSend = new EmailSend();
            emailSend.setEmailAddress(config.getSenderAddress());
            emailSend.setData(objectMapper.writeValueAsString(emailData));
            emailSend.setMessageId(config.getMessageId());
            emailSend.setAppId(config.getApplicationId());
            emailSend.setMessageType(config.getMessageType());
            emailSend.setCreatedAt(dateGenerator.generate()
                    .format(DateTimeFormatter.ofPattern(config.getDateFormat())));
            return emailSend;
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to map order: " + ordersApiWrappable.getOrdersApi().getReference(), e);
        }
    }
}
