package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class Certificate {
    private final String id;
    private final String companyNumber;
    private final String certificateType;
    private final String deliveryMethod;
    private final Integer quantity;
    private final String fee;

    public Certificate(String id, String companyNumber, String certificateType, String deliveryMethod, Integer quantity, String fee) {
        this.id = id;
        this.companyNumber = companyNumber;
        this.certificateType = certificateType;
        this.deliveryMethod = deliveryMethod;
        this.quantity = quantity;
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public String getFee() {
        return fee;
    }
    public Integer getQuantity() {
        return quantity;
    }

    public static CertificateBuilder builder() {
        return new CertificateBuilder();
    }

    static class CertificateBuilder implements Cloneable {
        private String id;
        private String certificateType;
        private String companyNumber;
        private String deliveryMethod;
        private String fee;
        private Integer quantity;

        @Override
        protected CertificateBuilder clone() {
            try {
                return (CertificateBuilder) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public CertificateBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public CertificateBuilder withCertificateType(String certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public CertificateBuilder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public CertificateBuilder withDeliveryMethod(String deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
            return this;
        }

        public CertificateBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public CertificateBuilder withFee(String fee) {
            this.fee = fee;
            return this;
        }

        public Certificate build() {
            return new Certificate(id, companyNumber, certificateType, deliveryMethod, quantity, fee);
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
        Certificate that = (Certificate) o;
        return Objects.equals(id, that.id)
                && Objects.equals(certificateType, that.certificateType)
                && Objects.equals(companyNumber, that.companyNumber)
                && Objects.equals(deliveryMethod, that.deliveryMethod)
                && Objects.equals(quantity, that.quantity)
                && Objects.equals(fee, that.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, certificateType, companyNumber, deliveryMethod, quantity, fee);
    }
}
