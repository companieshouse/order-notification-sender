package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class OrdersApiDetailsCommonFieldsMapper {
    private final EmailConfiguration emailConfiguration;

    public OrdersApiDetailsCommonFieldsMapper(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    void mapCommonFields(OrderModel orderModel, OrdersApiDetails ordersApiDetails) {
        OrdersApi ordersApi = ordersApiDetails.getOrdersApi();
        orderModel.setTo(ordersApi.getOrderedBy().getEmail());
        orderModel.setSubject(MessageFormat.format(emailConfiguration.getConfirmationMessage(),
                ordersApi.getReference()));
        orderModel.setCompanyName(ordersApiDetails.getBaseItemApi().getCompanyName());
        orderModel.setCompanyNumber(ordersApiDetails.getBaseItemApi().getCompanyNumber());
        orderModel.setOrderReferenceNumber(ordersApi.getReference());
        orderModel.setAmountPaid("£" + ordersApi.getTotalOrderCost());
        orderModel.setPaymentReference(ordersApi.getPaymentReference());
        orderModel.setPaymentTime(ordersApi.getOrderedAt()
                .format(DateTimeFormatter.ofPattern(emailConfiguration.getPaymentDateFormat())));
    }
}
