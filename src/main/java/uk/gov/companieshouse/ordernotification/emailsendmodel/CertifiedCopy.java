package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;
import java.util.Objects;

public class CertifiedCopy {
    private String id;
    private String companyNumber;
    private String deliveryMethod;
    private List<FilingHistoryDetailsModel> filingHistoryDetailsModelList;

    public CertifiedCopy(String id,
            String companyNumber,
            String deliveryMethod,
            List<FilingHistoryDetailsModel> filingHistoryDetailsModelList) {
        this.id = id;
        this.companyNumber = companyNumber;
        this.deliveryMethod = deliveryMethod;
        this.filingHistoryDetailsModelList = filingHistoryDetailsModelList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public List<FilingHistoryDetailsModel> getFilingHistoryDetailsModelList() {
        return filingHistoryDetailsModelList;
    }

    public void setFilingHistoryDetailsModelList(List<FilingHistoryDetailsModel> filingHistoryDetailsModelList) {
        this.filingHistoryDetailsModelList = filingHistoryDetailsModelList;
    }

    public static CertifiedCopyBuilder builder() {
        return new CertifiedCopyBuilder();
    }

    static class CertifiedCopyBuilder implements Cloneable {
        private String id;
        private String companyNumber;
        private String deliveryMethod;
        private List<FilingHistoryDetailsModel> filingHistoryDetailsModelList;

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

        public CertifiedCopyBuilder withFilingHistoryDetailsModelList(
                List<FilingHistoryDetailsModel> filingHistoryDetailsModelList) {
            this.filingHistoryDetailsModelList = filingHistoryDetailsModelList;
            return this;
        }

        public CertifiedCopy build() {
            return new CertifiedCopy(id, companyNumber, deliveryMethod,
                    filingHistoryDetailsModelList);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CertifiedCopyBuilder that = (CertifiedCopyBuilder) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(companyNumber, that.companyNumber) &&
                    Objects.equals(deliveryMethod, that.deliveryMethod) &&
                    Objects.equals(filingHistoryDetailsModelList,
                            that.filingHistoryDetailsModelList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, companyNumber, deliveryMethod, filingHistoryDetailsModelList);
        }
    }
}
