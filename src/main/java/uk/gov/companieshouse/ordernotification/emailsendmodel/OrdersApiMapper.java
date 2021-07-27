package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;

import java.time.format.DateTimeFormatter;

public abstract class OrdersApiMapper {

    private final DateGenerator dateGenerator;
    private final String dateFormat;
    private final String senderEmail;
    private final ObjectMapper mapper;

    public OrdersApiMapper(DateGenerator dateGenerator, String dateFormat, String senderEmail) {
        this.dateGenerator = dateGenerator;
        this.dateFormat = dateFormat;
        this.senderEmail = senderEmail;
        this.mapper = new ObjectMapper();
    }

    public EmailSend map(OrdersApi order) {
        try {
            return (EmailSend)EmailSend.newBuilder()
                    .setEmailAddress(senderEmail)
                    .setData(this.mapper.writeValueAsString(generateEmailData(order)))
                    .setMessageId(getMessageId())
                    .setAppId(getApplicationId())
                    .setMessageType(getMessageType())
                    .setCreatedAt(dateGenerator.generate().format(DateTimeFormatter.ofPattern(dateFormat)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    abstract OrderModel generateEmailData(OrdersApi order);

    abstract String getMessageId();

    abstract String getApplicationId();

    abstract String getMessageType();
}
