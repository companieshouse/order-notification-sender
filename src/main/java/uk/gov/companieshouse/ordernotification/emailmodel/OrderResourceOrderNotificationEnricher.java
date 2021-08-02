package uk.gov.companieshouse.ordernotification.emailmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrderMapperFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

import java.util.Map;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_URI;

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
     * @param orderReference the order responsible for triggering the notification
     */
    public EmailSend enrich(final String orderReference) throws OrdersResponseException {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, ORDER_URI, orderReference);
        loggingUtils.getLogger().debug("Fetching resource for order", logMap);
        OrdersApi order = orderRetrievable.getOrderData(orderReference);
        loggingUtils.logIfNotNull(logMap, ORDER_URI, order.getReference());
        loggingUtils.getLogger().debug("Mapping order", logMap);
        return orderMapperFactory.getOrderMapper(order.getItems().get(0).getKind()).map(order);
    }
}
