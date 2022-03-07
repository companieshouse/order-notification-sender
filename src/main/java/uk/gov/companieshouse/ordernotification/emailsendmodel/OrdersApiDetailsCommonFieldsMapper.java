package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class OrdersApiDetailsCommonFieldsMapper {
    private final EmailConfiguration emailConfiguration;

    public OrdersApiDetailsCommonFieldsMapper(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    void mapCommonFields(OrderModel orderModel, OrdersApiDetails ordersApiDetails) {
        orderModel.setTo(ordersApiDetails.getOrderEmail());
        orderModel.setSubject(MessageFormat.format(emailConfiguration.getConfirmationMessage(), ordersApiDetails.getOrderReference()));
        orderModel.setCompanyName(ordersApiDetails.getCompanyName());
        orderModel.setCompanyNumber(ordersApiDetails.getCompanyNumber());
        orderModel.setOrderReferenceNumber(ordersApiDetails.getOrderReference());
        orderModel.setAmountPaid("£" + ordersApiDetails.getTotalOrderCost());
        orderModel.setPaymentReference(ordersApiDetails.getPaymentReference());
        orderModel.setPaymentTime(ordersApiDetails.getOrderedAt()
                .format(DateTimeFormatter.ofPattern(emailConfiguration.getPaymentDateFormat())));
    }
}
