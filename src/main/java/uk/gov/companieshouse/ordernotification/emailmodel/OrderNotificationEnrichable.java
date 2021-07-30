package uk.gov.companieshouse.ordernotification.emailmodel;

import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

/**
 * An enrichment that fetches a resource using a provided order reference number and maps it to
 * {@link EmailSend email data}.
 */
public interface OrderNotificationEnrichable {

    /**
     * Fetch a resource using the provided order reference and map it to {@link EmailSend email data}.
     *
     * @param orderReference The order reference number.
     * @return An {@link EmailSend object} containing data that will be used by the email client.
     * @throws OrdersResponseException If an error is raised when retrieving the order resource.
     */
    EmailSend enrich(String orderReference) throws OrdersResponseException;
}
