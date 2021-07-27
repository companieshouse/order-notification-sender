package uk.gov.companieshouse.ordernotification.ordersprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.ordernotification.orders.model.OrderData;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersService;

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
public class OrderProcessorService {

    private final OrdersService ordersService;
    private final LoggingUtils loggingUtils;

    @Autowired
    public OrderProcessorService(final OrdersService ordersService, LoggingUtils loggingUtils) {
        this.ordersService = ordersService;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Implements all of the business logic required to process the notification of an order received.
     * @param orderUri the URI representing the order received
     */
    @EventListener
    public void processOrderReceived(final SendOrderNotificationEvent event) throws Exception {
        final OrderData order;
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, ORDER_URI, event.getOrderReference());
        try {
            order = ordersService.getOrderData(event.getOrderReference());
        } catch (Exception ex) {
            loggingUtils.getLogger().error("Exception caught getting order data.", ex, logMap);
            throw ex;
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
