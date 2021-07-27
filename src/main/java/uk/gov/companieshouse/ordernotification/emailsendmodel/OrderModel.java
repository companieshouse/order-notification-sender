package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class OrderModel {

    private String orderReferenceNumber;
    private String totalOrderCost;
    private String paymentReference;
    private String paymentTime;

    public String getOrderReferenceNumber() {
        return orderReferenceNumber;
    }

    public void setOrderReferenceNumber(String orderReferenceNumber) {
        this.orderReferenceNumber = orderReferenceNumber;
    }

    public String getTotalOrderCost() {
        return totalOrderCost;
    }

    public void setTotalOrderCost(String totalOrderCost) {
        this.totalOrderCost = totalOrderCost;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderModel)) {
            return false;
        }
        OrderModel that = (OrderModel) o;
        return Objects.equals(getOrderReferenceNumber(), that.getOrderReferenceNumber()) &&
                Objects.equals(getTotalOrderCost(), that.getTotalOrderCost()) &&
                Objects.equals(getPaymentReference(), that.getPaymentReference()) &&
                Objects.equals(getPaymentTime(), that.getPaymentTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderReferenceNumber(), getTotalOrderCost(), getPaymentReference(), getPaymentTime());
    }
}
