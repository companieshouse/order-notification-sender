package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.orders.OrderReceived;

@Service
public class OrderMessageHandler {

    private final OrderProcessorService orderProcessorService;
    private final OrderProcessResponseHandler orderProcessResponseHandler;
    private final MessageFilter<OrderReceived> messageFilter;
    private final Logger logger;
    private final LoggingUtils loggingUtils;

    public OrderMessageHandler(final OrderProcessorService orderProcessorService,
                               final OrderProcessResponseHandler orderProcessResponseHandler,
                               final MessageFilter<OrderReceived> messageFilter,
                               final Logger logger, final LoggingUtils loggingUtils) {
        this.orderProcessorService = orderProcessorService;
        this.orderProcessResponseHandler = orderProcessResponseHandler;
        this.messageFilter = messageFilter;
        this.logger = logger;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Handles processing of received message.
     *
     * @param message received
     */
    public void handleMessage(Message<OrderReceived> message) {
        if (messageFilter.include(message)) {
            // Log message
            logger.info("'order-received' message received", loggingUtils.getMessageHeadersAsMap(message));

            // Process message
            OrderProcessResponse response = orderProcessorService.processOrderReceived(message.getPayload().getOrderUri());

            // Handle response
            response.getStatus().accept(orderProcessResponseHandler, message);
        }
    }
}