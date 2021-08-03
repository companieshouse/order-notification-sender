package uk.gov.companieshouse.ordernotification.emailmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MappingException;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrderMapperFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

import java.util.Map;

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
    private final OrderMapperFactory orderMapperFactory;

    @Autowired
    public OrderResourceOrderNotificationEnricher(final OrderRetrievable orderRetrievable, OrderMapperFactory orderMapperFactory, LoggingUtils loggingUtils) {
        this.orderRetrievable = orderRetrievable;
        this.orderMapperFactory = orderMapperFactory;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Enriches an order received notification with an order resource fetched from the Orders API.
     *
     * @param orderUri the order responsible for triggering the notification
     */
    public EmailSend enrich(final String orderUri) throws OrdersResponseException {
        Map<String, Object> logArgs = loggingUtils.logWithOrderUri("Fetching resource for order", orderUri);
        OrdersApi order = orderRetrievable.getOrderData(orderUri);
        loggingUtils.getLogger().debug("Mapping order", logArgs);
        try {
            return orderMapperFactory.getOrderMapper(order.getItems().get(0).getKind()).map(order);
        } catch (IllegalArgumentException | MappingException e) {
            loggingUtils.getLogger().error("Failed to map order resource", e, logArgs);
            throw e;
        }
    }
}
