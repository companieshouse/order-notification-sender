package uk.gov.companieshouse.ordernotification.emailsendmodel;


public interface KindMapper {
    OrderDetails map(OrdersApiDetails ordersApiDetails);
}
