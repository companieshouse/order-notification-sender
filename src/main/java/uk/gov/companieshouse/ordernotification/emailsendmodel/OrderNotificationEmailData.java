package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;

public class OrderNotificationEmailData {
    private String orderId;
    private List<Certificate> certificates;
    private List<CertifiedCopy> certifiedCopies;
    private List<MissingImageDelivery> missingImageDeliveries;
    private DeliveryDetails deliveryDetails;
    private PaymentDetails paymentDetails;
    private boolean hasStandardDelivery;
    private boolean hasExpressDelivery;
    private String orderSummaryLink;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<CertifiedCopy> getCertifiedCopies() {
        return certifiedCopies;
    }

    public void setCertifiedCopies(List<CertifiedCopy> certifiedCopies) {
        this.certifiedCopies = certifiedCopies;
    }

    public List<MissingImageDelivery> getMissingImageDeliveries() {
        return missingImageDeliveries;
    }

    public void setMissingImageDeliveries(List<MissingImageDelivery> missingImageDeliveries) {
        this.missingImageDeliveries = missingImageDeliveries;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(DeliveryDetails deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public boolean isHasStandardDelivery() {
        return hasStandardDelivery;
    }

    public void setHasStandardDelivery(boolean hasStandardDelivery) {
        this.hasStandardDelivery = hasStandardDelivery;
    }

    public boolean isHasExpressDelivery() {
        return hasExpressDelivery;
    }

    public void setHasExpressDelivery(boolean hasExpressDelivery) {
        this.hasExpressDelivery = hasExpressDelivery;
    }

    public String getOrderSummaryLink() {
        return orderSummaryLink;
    }

    public void setOrderSummaryLink(String orderSummaryLink) {
        this.orderSummaryLink = orderSummaryLink;
    }
}
