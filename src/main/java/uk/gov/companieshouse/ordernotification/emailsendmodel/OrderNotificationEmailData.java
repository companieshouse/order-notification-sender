package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class OrderNotificationEmailData {
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

    public boolean hasStandardDelivery() {
        return hasStandardDelivery;
    }

    public void hasStandardDelivery(boolean hasStandardDelivery) {
        this.hasStandardDelivery = hasStandardDelivery;
    }

    public boolean hasExpressDelivery() {
        return hasExpressDelivery;
    }

    public void hasExpressDelivery(boolean hasExpressDelivery) {
        this.hasExpressDelivery = hasExpressDelivery;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderNotificationEmailData emailData = (OrderNotificationEmailData) o;
        return hasStandardDelivery == emailData.hasStandardDelivery && hasExpressDelivery == emailData.hasExpressDelivery && dispatchDays == emailData.dispatchDays && Objects.equals(orderId, emailData.orderId) && Objects.equals(certificates, emailData.certificates) && Objects.equals(certifiedCopies, emailData.certifiedCopies) && Objects.equals(missingImageDeliveries, emailData.missingImageDeliveries) && Objects.equals(deliveryDetails, emailData.deliveryDetails) && Objects.equals(paymentDetails, emailData.paymentDetails) && Objects.equals(orderSummaryLink, emailData.orderSummaryLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, certificates, certifiedCopies, missingImageDeliveries, deliveryDetails, paymentDetails, hasStandardDelivery, hasExpressDelivery, orderSummaryLink, dispatchDays);
    }
}
