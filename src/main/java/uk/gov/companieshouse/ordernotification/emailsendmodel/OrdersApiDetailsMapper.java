package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;

/**
 * Map an {@link OrdersApi} object to an {@link EmailSend} object.
 */
@Component
public class OrdersApiDetailsMapper {

    private final DateGenerator dateGenerator;
    private final EmailConfiguration config;
    private final ObjectMapper objectMapper;
    private final KindMapperFactory kindMapperFactory;

    public OrdersApiDetailsMapper(DateGenerator dateGenerator,
                                  EmailConfiguration config,
                                  ObjectMapper objectMapper,
                                  KindMapperFactory kindMapperFactory) {
        this.dateGenerator = dateGenerator;
        this.config = config;
        this.objectMapper = objectMapper;
        this.kindMapperFactory = kindMapperFactory;
    }

    /**
     * Map an {@link OrderDetails} object to an {@link EmailSend} object.
     *
     * @return An {@link EmailSend} object containing orderDetails data.
     * @throws MappingException If an error occurs when serialising email data.
     */
    public EmailSend mapToEmailSend(OrdersApiDetails ordersApiDetails) {
        OrderDetails orderDetails = kindMapperFactory.getInstance(ordersApiDetails.getKind()).map(
                ordersApiDetails);
        try {
            EmailSend emailSend = new EmailSend();
            emailSend.setEmailAddress(config.getSenderAddress());
            emailSend.setData(objectMapper.writeValueAsString(orderDetails.getOrderModel()));
            emailSend.setMessageId(orderDetails.getMessageId());
            emailSend.setAppId(config.getApplicationId());
            emailSend.setMessageType(orderDetails.getMessageType());
            emailSend.setCreatedAt(dateGenerator.generate()
                    .format(DateTimeFormatter.ofPattern(config.getDateFormat())));
            return emailSend;
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to map orderDetails: " + ordersApiDetails.getReference(), e);
        }
    }
}
