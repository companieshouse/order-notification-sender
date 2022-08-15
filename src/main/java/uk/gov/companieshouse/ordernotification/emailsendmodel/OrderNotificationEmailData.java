package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderNotificationEmailData {
    private String to;
    private String subject;
    private String orderId;
    private List<Certificate> certificates = new ArrayList<>();
    private List<CertifiedCopy> certifiedCopies  = new ArrayList<>();;
    private List<MissingImageDelivery> missingImageDeliveries = new ArrayList<>();;
    private DeliveryDetails deliveryDetails;
    private PaymentDetails paymentDetails;
    private boolean hasStandardDelivery;
    private boolean hasExpressDelivery;
    private String orderSummaryLink;
    private int dispatchDays;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

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

    public void addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
    }

    public List<CertifiedCopy> getCertifiedCopies() {
        return certifiedCopies;
    }

    public void setCertifiedCopies(List<CertifiedCopy> certifiedCopies) {
        this.certifiedCopies = certifiedCopies;
    }

    public void addCertifiedCopy(CertifiedCopy certifiedCopy) {
        this.certifiedCopies.add(certifiedCopy);
    }

    public List<MissingImageDelivery> getMissingImageDeliveries() {
        return missingImageDeliveries;
    }

    public void setMissingImageDeliveries(List<MissingImageDelivery> missingImageDeliveries) {
        this.missingImageDeliveries = missingImageDeliveries;
    }

    public void addMissingImageDelivery(MissingImageDelivery missingImageDelivery) {
        this.missingImageDeliveries.add(missingImageDelivery);
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

    public String getOrderSummaryLink() {
        return orderSummaryLink;
    }

    public void setOrderSummaryLink(String orderSummaryLink) {
        this.orderSummaryLink = orderSummaryLink;
    }

    public int getDispatchDays() {
        return dispatchDays;
    }

    public void setDispatchDays(int dispatchDays) {
        this.dispatchDays = dispatchDays;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderNotificationEmailData that = (OrderNotificationEmailData) o;
        return hasStandardDelivery == that.hasStandardDelivery && hasExpressDelivery == that.hasExpressDelivery && dispatchDays == that.dispatchDays && Objects.equals(to, that.to) && Objects.equals(subject, that.subject) && Objects.equals(orderId, that.orderId) && Objects.equals(certificates, that.certificates) && Objects.equals(certifiedCopies, that.certifiedCopies) && Objects.equals(missingImageDeliveries, that.missingImageDeliveries) && Objects.equals(deliveryDetails, that.deliveryDetails) && Objects.equals(paymentDetails, that.paymentDetails) && Objects.equals(orderSummaryLink, that.orderSummaryLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, subject, orderId, certificates, certifiedCopies, missingImageDeliveries, deliveryDetails, paymentDetails, hasStandardDelivery, hasExpressDelivery, orderSummaryLink, dispatchDays);
    }
}
