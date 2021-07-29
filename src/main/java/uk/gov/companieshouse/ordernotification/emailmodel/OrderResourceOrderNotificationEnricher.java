package uk.gov.companieshouse.ordernotification.emailmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.ordernotification.orders.model.OrderData;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiService;

import java.util.Map;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_REFERENCE_NUMBER;
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

    private final OrdersApiService ordersApiService;
    private final LoggingUtils loggingUtils;

    @Autowired
    public OrderResourceOrderNotificationEnricher(final OrdersApiService ordersApiService, LoggingUtils loggingUtils) {
        this.ordersApiService = ordersApiService;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Enriches an order received notification with an order resource fetched from the Orders API.
     *
     * @param event the order responsible for triggering the notification
     */
    @EventListener
    public void enrich(final SendOrderNotificationEvent event) {
        final OrdersApi order;
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, ORDER_URI, event.getOrderReference());
        try {
            order = ordersApiService.getOrderData(event.getOrderReference());
        } catch (Exception ex) {
            loggingUtils.getLogger().error("Exception caught getting order data.", ex, logMap);
            throw new RuntimeException(ex);
        }
        loggingUtils.logIfNotNull(logMap, ORDER_REFERENCE_NUMBER, order.getReference());
        loggingUtils.getLogger().info("Processing order received", logMap);
        try {
            //orderRouter.routeOrder(order); TODO: create new email sender
        } catch (Exception ex) {
            loggingUtils.getLogger().error("Exception caught routing order.", ex, logMap);
            throw ex;
        }

    }
}
