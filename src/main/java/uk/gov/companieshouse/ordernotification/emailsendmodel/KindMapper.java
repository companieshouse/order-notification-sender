package uk.gov.companieshouse.ordernotification.emailsendmodel;


import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

public interface KindMapper {
    OrderDetails map(OrdersApiDetails ordersApiDetails);
}
