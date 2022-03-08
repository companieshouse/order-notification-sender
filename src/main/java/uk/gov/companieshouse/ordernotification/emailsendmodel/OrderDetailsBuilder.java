package uk.gov.companieshouse.ordernotification.emailsendmodel;

final class OrderDetailsBuilder {

    private OrderModel orderModel;
    private String messageId;
    private String messageType;

    private OrderDetailsBuilder() {
    }

    static OrderDetailsBuilder newBuilder() {
        return new OrderDetailsBuilder();
    }

    OrderDetailsBuilder withOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
        return this;
    }

    OrderDetailsBuilder withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    OrderDetailsBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    OrderDetails build() {
        return new OrderDetailsObject(this);
    }

    static final class OrderDetailsObject implements OrderDetails {
        private final OrderModel orderModel;
        private final String messageId;
        private final String messageType;

        private OrderDetailsObject(OrderDetailsBuilder builder) {
            orderModel = builder.orderModel;
            messageId = builder.messageId;
            messageType = builder.messageType;
        }

        @Override
        public OrderModel getOrderModel() {
            return orderModel;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Override
        public String getMessageType() {
            return messageType;
        }
    }
}
