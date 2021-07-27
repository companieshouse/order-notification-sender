package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;

import java.time.format.DateTimeFormatter;

public abstract class OrdersApiMapper {

    private final DateGenerator dateGenerator;
    private final String dateFormat;
    private final String paymentDateFormat;
    private final String senderEmail;
    private final ObjectMapper mapper;

    public OrdersApiMapper(DateGenerator dateGenerator, String dateFormat, String paymentDateFormat, String senderEmail) {
        this.dateGenerator = dateGenerator;
        this.dateFormat = dateFormat;
        this.paymentDateFormat = paymentDateFormat;
        this.senderEmail = senderEmail;
        this.mapper = new ObjectMapper();
    }

    public EmailSend map(OrdersApi order) {
        try {
            EmailSend emailSend = new EmailSend();
            emailSend.setEmailAddress(senderEmail);
            emailSend.setData(this.mapper.writeValueAsString(addOrderMetadata(generateEmailData(order.getItems().get(0)), order)));
            emailSend.setMessageId(getMessageId());
            emailSend.setAppId(getApplicationId());
            emailSend.setMessageType(getMessageType());
            emailSend.setCreatedAt(dateGenerator.generate().format(DateTimeFormatter.ofPattern(dateFormat)));
            return emailSend;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    abstract OrderModel generateEmailData(BaseItemApi order);

    abstract String getMessageId();

    abstract String getApplicationId();

    abstract String getMessageType();

    private OrderModel addOrderMetadata(OrderModel model, OrdersApi order) {
        model.setOrderReferenceNumber(order.getReference());
        model.setTotalOrderCost(order.getTotalOrderCost());
        model.setPaymentReference(order.getPaymentReference());
        model.setPaymentTime(order.getOrderedAt().format(DateTimeFormatter.ofPattern(paymentDateFormat)));
        return model;
    }
}