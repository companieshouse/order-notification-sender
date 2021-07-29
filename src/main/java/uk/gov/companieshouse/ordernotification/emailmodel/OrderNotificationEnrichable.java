package uk.gov.companieshouse.ordernotification.emailmodel;

import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

public interface OrderNotificationEnrichable {
    EmailSend enrich(String orderUri) throws OrdersResponseException;
}
