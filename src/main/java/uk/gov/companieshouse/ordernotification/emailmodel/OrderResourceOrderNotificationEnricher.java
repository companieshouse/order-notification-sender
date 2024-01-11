package uk.gov.companieshouse.ordernotification.emailmodel;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MappingException;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetailsMapper;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

/**
 * This service does the following:
 * <ol>
 *     <li>handles order received notification</li>
 *     <li>retrieves the order (data) from the Orders API</li>
 *     <li>sends a certificate or certified copy order confirmation via the CHS Email Sender OR</li>
 *     <li>sends a MID item message to the CHD Order Consumer</li>
 * </ol>
 */
@Service
public class OrderResourceOrderNotificationEnricher implements OrderNotificationEnrichable {

    private final OrderRetrievable orderRetrievable;
    private final LoggingUtils loggingUtils;
    private final OrdersApiDetailsMapper ordersApiMapper;

    @Autowired
    public OrderResourceOrderNotificationEnricher(OrderRetrievable orderRetrievable,
                                                  LoggingUtils loggingUtils,
                                                  OrdersApiDetailsMapper ordersApiMapper) {
        this.orderRetrievable = orderRetrievable;
        this.loggingUtils = loggingUtils;
        this.ordersApiMapper = ordersApiMapper;
    }

    /**
     * Enriches an order received notification with an order resource fetched from the Orders API.
     *
     * @param orderUri the order responsible for triggering the notification
     */
    public EmailSend enrich(final String orderUri) throws OrdersResponseException {
        Map<String, Object> logArgs = loggingUtils.logWithOrderUri("Fetching resource for order", orderUri);
        OrdersApiWrappable order = orderRetrievable.getOrderData(orderUri);
        loggingUtils.getLogger().debug("Mapping order", logArgs);
        try {
            return ordersApiMapper.mapToEmailSend(order);
        } catch (IllegalArgumentException | MappingException e) {
            loggingUtils.getLogger().error("Failed to map order resource", e, logArgs);
            throw e;
        }
    }

    /**
     * Enriches an item ready notification with an order resource fetched from the Orders API, and
     * with item ready information
     *
     * @param orderUri the order responsible for triggering the notification
     * @param itemReady the incoming item ready notification
     */
    public EmailSend enrich(final String orderUri, final ItemGroupProcessedSend itemReady) throws OrdersResponseException {
        Map<String, Object> logArgs = loggingUtils.logWithOrderUri("Fetching resource for order", orderUri);
        OrdersApiWrappable order = orderRetrievable.getOrderData(orderUri);
        loggingUtils.getLogger().debug("Mapping order", logArgs);
        try {
            return ordersApiMapper.mapToEmailSend(order, itemReady);
        } catch (IllegalArgumentException | MappingException e) {
            loggingUtils.getLogger().error("Failed to map order resource", e, logArgs);
            throw e;
        }
    }
}
