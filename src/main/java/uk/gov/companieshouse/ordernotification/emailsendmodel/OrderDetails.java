package uk.gov.companieshouse.ordernotification.emailsendmodel;

public interface OrderDetails {
    OrderModel getOrderModel();
    String getMessageId();
    String getMessageType();
}
