package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class MissingImageDelivery {
    private final String id;
    private final String dateFiled;
    private final String type;
    private final String description;
    private final String companyNumber;
    private final String fee;

    public MissingImageDelivery(String id, String dateFiled, String type, String description,
                                String companyNumber, String fee) {
        this.id = id;
        this.dateFiled = dateFiled;
        this.type = type;
        this.description = description;
        this.companyNumber = companyNumber;
        this.fee = fee;
    }

    public String getId() {
        return id;
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

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getFee() {
        return fee;
    }

    public static MissingImageDeliveryBuilder builder() {
        return new MissingImageDeliveryBuilder();
    }

    static class MissingImageDeliveryBuilder {
        private String id;
        private String dateFiled;
        private String type;
        private String description;
        private String companyNumber;
        private String fee;

        public MissingImageDeliveryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public MissingImageDeliveryBuilder withDateFiled(String dateFiled) {
            this.dateFiled = dateFiled;
            return this;
        }

        public MissingImageDeliveryBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public MissingImageDeliveryBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public MissingImageDeliveryBuilder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public MissingImageDeliveryBuilder withFee(String fee) {
            this.fee = fee;
            return this;
        }

        public MissingImageDelivery build() {
            return new MissingImageDelivery(id, dateFiled, type, description, companyNumber, fee);
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
        MissingImageDelivery that = (MissingImageDelivery) o;
        return Objects.equals(id, that.id) && Objects.equals(dateFiled, that.dateFiled) && Objects.equals(type, that.type) && Objects.equals(description, that.description) && Objects.equals(companyNumber, that.companyNumber) && Objects.equals(fee, that.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateFiled, type, description, companyNumber, fee);
    }
}
