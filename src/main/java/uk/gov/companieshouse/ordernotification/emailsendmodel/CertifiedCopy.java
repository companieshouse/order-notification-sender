package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertifiedCopy {
    private String id;
    private String companyNumber;
    private String deliveryMethod;
    private String dateFiled;
    private String type;
    private String description;
    private String fee;

    public CertifiedCopy(String id, String companyNumber, String deliveryMethod,
            String dateFiled, String type, String description, String fee) {
        this.id = id;
        this.companyNumber = companyNumber;
        this.deliveryMethod = deliveryMethod;
        this.dateFiled = dateFiled;
        this.type = type;
        this.description = description;
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public String getDateFiled() {
        return dateFiled;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getFee() {
        return fee;
    }

    public static CertifiedCopyBuilder builder() {
        return new CertifiedCopyBuilder();
    }

    static class CertifiedCopyBuilder implements Cloneable {
        private String id;
        private String companyNumber;
        private String deliveryMethod;
        private String dateFiled;
        private String type;
        private String description;
        private String fee;

        @Override
        protected CertifiedCopyBuilder clone() {
            try {
                return (CertifiedCopyBuilder) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public CertifiedCopyBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public CertifiedCopyBuilder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public CertifiedCopyBuilder withDeliveryMethod(String deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
            return this;
        }

        public CertifiedCopyBuilder withDateFiled(String dateFiled) {
            this.dateFiled = dateFiled;
            return this;
        }

        public CertifiedCopyBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public CertifiedCopyBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CertifiedCopyBuilder withFee(String fee) {
            this.fee = fee;
            return this;
        }

        public CertifiedCopy build() {
            return new CertifiedCopy(id, companyNumber, deliveryMethod,
                    dateFiled, type, description, fee);
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
        CertifiedCopy that = (CertifiedCopy) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(companyNumber, that.companyNumber) &&
                Objects.equals(deliveryMethod, that.deliveryMethod) &&
                Objects.equals(dateFiled, that.dateFiled) &&
                Objects.equals(type, that.type) &&
                Objects.equals(description, that.description) &&
                Objects.equals(fee, that.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyNumber, deliveryMethod, dateFiled, type, description, fee);
    }
}
