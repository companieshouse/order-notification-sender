package uk.gov.companieshouse.ordernotification.emailsendmodel;


import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

public interface OrderKindMapper {
    OrderDetails map(OrdersApiDetails ordersApiDetails);
}
