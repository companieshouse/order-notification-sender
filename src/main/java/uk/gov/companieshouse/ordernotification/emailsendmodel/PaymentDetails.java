package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class PaymentDetails {
    private String paymentReference;
    private String amountPaid;
    private String paymentDate;

    public PaymentDetails(String paymentReference, String amountPaid, String paymentDate) {
        this.paymentReference = paymentReference;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public static PaymentDetailsBuilder builder() {
        return new PaymentDetailsBuilder();
    }

    static class PaymentDetailsBuilder {
        private String paymentReference;
        private String amountPaid;
        private String paymentDate;

        public PaymentDetailsBuilder withPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
            return this;
        }

        public PaymentDetailsBuilder withAmountPaid(String amountPaid) {
            this.amountPaid = amountPaid;
            return this;
        }

        public PaymentDetailsBuilder withPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public PaymentDetails build() {
            return new PaymentDetails(paymentReference, amountPaid, paymentDate);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentDetails that = (PaymentDetails) o;
        return Objects.equals(paymentReference, that.paymentReference) &&
                Objects.equals(amountPaid, that.amountPaid) &&
                Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentReference, amountPaid, paymentDate);
    }
}
