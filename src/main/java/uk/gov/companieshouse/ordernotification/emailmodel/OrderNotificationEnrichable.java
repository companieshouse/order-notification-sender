package uk.gov.companieshouse.ordernotification.emailmodel;

import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;

public interface OrderNotificationEnrichable {
    void enrich(final SendOrderNotificationEvent event);
}
