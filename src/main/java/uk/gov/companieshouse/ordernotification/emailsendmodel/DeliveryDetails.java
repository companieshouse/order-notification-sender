package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class DeliveryDetails {
    private final String addressLine1;
    private final String addressLine2;
    private final String country;
    private final String locality;
    private final String poBox;
    private final String postalCode;
    private final String region;
    private final String forename;
    private final String surname;

    public DeliveryDetails(String addressLine1, String addressLine2, String country,
            String locality, String poBox, String postalCode, String region,
            String forename, String surname) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.locality = locality;
        this.poBox = poBox;
        this.postalCode = postalCode;
        this.region = region;
        this.forename = forename;
        this.surname = surname;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getPoBox() {
        return poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getRegion() {
        return region;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public static DeliveryDetailsBuilder builder() {
        return new DeliveryDetailsBuilder();
    }

    static class DeliveryDetailsBuilder {
        private String addressLine1;
        private String addressLine2;
        private String country;
        private String locality;
        private String poBox;
        private String postalCode;
        private String region;
        private String forename;
        private String surname;

        public DeliveryDetailsBuilder withAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public DeliveryDetailsBuilder withAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public DeliveryDetailsBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public DeliveryDetailsBuilder withLocality(String locality) {
            this.locality = locality;
            return this;
        }

        public DeliveryDetailsBuilder withPoBox(String poBox) {
            this.poBox = poBox;
            return this;
        }

        public DeliveryDetailsBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public DeliveryDetailsBuilder withRegion(String region) {
            this.region = region;
            return this;
        }

        public DeliveryDetailsBuilder withForename(String forename) {
            this.forename = forename;
            return this;
        }

        public DeliveryDetailsBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public DeliveryDetails build() {
            return new DeliveryDetails(addressLine1, addressLine2, country, locality, poBox,
                    postalCode, region, forename, surname);
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
        DeliveryDetails that = (DeliveryDetails) o;
        return Objects.equals(addressLine1, that.addressLine1) &&
                Objects.equals(addressLine2, that.addressLine2) &&
                Objects.equals(country, that.country) &&
                Objects.equals(locality, that.locality) &&
                Objects.equals(poBox, that.poBox) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(region, that.region) &&
                Objects.equals(forename, that.forename) &&
                Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine1, addressLine2, country, locality, poBox, postalCode,
                region,
                forename, surname);
    }
}
