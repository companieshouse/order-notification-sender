package uk.gov.companieshouse.ordernotification.emailmodel;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MappingException;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetailsMapper;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

/**
 * This service does the following:
 * <ol>
 *     <li>handles item ready notification</li>
 *     <li>retrieves the order (data) from the Orders API</li>
 *     <li>sends a certificate or certified copy order confirmation via the CHS Email Sender</li>
 * </ol>
 */
@Service
public class OrderResourceItemReadyNotificationEnricher {

    private final OrderRetrievable orderRetrievable;
    private final LoggingUtils loggingUtils;
    private final OrdersApiDetailsMapper ordersApiMapper;

    @Autowired
    public OrderResourceItemReadyNotificationEnricher(OrderRetrievable orderRetrievable,
        LoggingUtils loggingUtils,
        OrdersApiDetailsMapper ordersApiMapper) {
        this.orderRetrievable = orderRetrievable;
        this.loggingUtils = loggingUtils;
        this.ordersApiMapper = ordersApiMapper;
    }

    /**
     * Enriches an item ready notification with an order resource fetched from the Orders API, and
     * with item ready information
     *
     * @param orderUri  the order responsible for triggering the notification
     * @param itemReadyNotification the incoming item ready notification
     */
    public EmailSend enrich(final String orderUri, final ItemGroupProcessedSend itemReadyNotification)
        throws OrdersResponseException {
        Map<String, Object> logArgs = loggingUtils.logWithOrderUri("Fetching resource for order",
            orderUri);
        OrdersApiWrappable order = orderRetrievable.getOrderData(orderUri);
        loggingUtils.getLogger().debug("Mapping order and item ready notification", logArgs);
        try {
            return ordersApiMapper.mapToEmailSend(order, itemReadyNotification);
        } catch (IllegalArgumentException | MappingException e) {
            loggingUtils.getLogger()
                .error("Failed to map order and item ready notification", e, logArgs);
            throw e;
        }
    }
}
