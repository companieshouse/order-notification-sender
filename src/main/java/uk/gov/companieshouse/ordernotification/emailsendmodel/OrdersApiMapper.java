package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

/**
 * Map an {@link OrdersApi} object to an {@link EmailSend} object.
 */
public abstract class OrdersApiMapper {

    private final DateGenerator dateGenerator;
    private final EmailConfiguration config;
    private final ObjectMapper mapper;

    public OrdersApiMapper(DateGenerator dateGenerator, EmailConfiguration config, ObjectMapper mapper) {
        this.dateGenerator = dateGenerator;
        this.config = config;
        this.mapper = mapper;
    }

    /**
     * Map an {@link OrdersApi} object to an {@link EmailSend} object.
     *
     * @param order An order resource returned by the orders API.
     * @return An {@link EmailSend} object containing order data.
     *
     * @throws MappingException If an error occurs when serialising email data.
     */
    public EmailSend map(OrdersApi order) {
        try {
            EmailSend emailSend = new EmailSend();
            emailSend.setEmailAddress(getConfig().getSenderAddress());
            emailSend.setData(this.mapper.writeValueAsString(addOrderMetadata(generateEmailData(order.getItems().get(0)), order)));
            emailSend.setMessageId(getMessageId());
            emailSend.setAppId(getConfig().getApplicationId());
            emailSend.setMessageType(getMessageType());
            emailSend.setCreatedAt(dateGenerator.generate().format(DateTimeFormatter.ofPattern(getConfig().getDateFormat())));
            return emailSend;
        } catch (JsonProcessingException e) {
            throw new MappingException("Failed to map order: " + order.getReference(), e);
        }
    }

    protected abstract OrderModel generateEmailData(BaseItemApi order);

    protected abstract String getMessageId();

    protected abstract String getMessageType();

    private OrderModel addOrderMetadata(OrderModel model, OrdersApi order) {
        model.setTo(order.getOrderedBy().getEmail());
        model.setSubject(MessageFormat.format(getConfig().getConfirmationMessage(), order.getReference()));
        model.setCompanyName(order.getItems().get(0).getCompanyName());
        model.setCompanyNumber(order.getItems().get(0).getCompanyNumber());
        model.setOrderReferenceNumber(order.getReference());
        model.setAmountPaid("£"+order.getTotalOrderCost());
        model.setPaymentReference(order.getPaymentReference());
        model.setPaymentTime(order.getOrderedAt().format(DateTimeFormatter.ofPattern(getConfig().getPaymentDateFormat())));
        return model;
    }

    protected final EmailConfiguration getConfig() {
        return config;
    }
}
