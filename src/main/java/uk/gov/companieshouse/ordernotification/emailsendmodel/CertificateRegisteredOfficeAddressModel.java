package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateRegisteredOfficeAddressModel {

    private String registeredOfficeAddressType;
    private boolean includeRegisteredOfficeAddressDates;

    public CertificateRegisteredOfficeAddressModel(String registeredOfficeAddressType, boolean includeRegisteredOfficeAddressDates) {
        this.registeredOfficeAddressType = registeredOfficeAddressType;
        this.includeRegisteredOfficeAddressDates = includeRegisteredOfficeAddressDates;
    }

    public String getRegisteredOfficeAddressType() {
        return registeredOfficeAddressType;
    }

    public void setRegisteredOfficeAddressType(String registeredOfficeAddressType) {
        this.registeredOfficeAddressType = registeredOfficeAddressType;
    }

    public boolean isIncludeRegisteredOfficeAddressDates() {
        return includeRegisteredOfficeAddressDates;
    }

    public void setIncludeRegisteredOfficeAddressDates(boolean includeRegisteredOfficeAddressDates) {
        this.includeRegisteredOfficeAddressDates = includeRegisteredOfficeAddressDates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateRegisteredOfficeAddressModel that = (CertificateRegisteredOfficeAddressModel) o;
        return includeRegisteredOfficeAddressDates == that.includeRegisteredOfficeAddressDates &&
                Objects.equals(registeredOfficeAddressType, that.registeredOfficeAddressType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredOfficeAddressType, includeRegisteredOfficeAddressDates);
    }
}
