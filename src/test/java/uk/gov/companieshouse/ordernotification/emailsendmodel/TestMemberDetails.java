package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.BaseMemberDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;

class TestMemberDetails implements BaseMemberDetailsApi {
    private Boolean includeAddress;
    private Boolean includeAppointmentDate;
    private Boolean includeBasicInformation;
    private Boolean includeCountryOfResidence;
    private IncludeDobTypeApi includeDobTypeApi;

    public TestMemberDetails(Boolean includeAddress, Boolean includeAppointmentDate, Boolean includeBasicInformation, Boolean includeCountryOfResidence, IncludeDobTypeApi includeDobTypeApi) {
        this.includeAddress = includeAddress;
        this.includeAppointmentDate = includeAppointmentDate;
        this.includeBasicInformation = includeBasicInformation;
        this.includeCountryOfResidence = includeCountryOfResidence;
        this.includeDobTypeApi = includeDobTypeApi;
    }

    @Override
    public Boolean getIncludeAddress() {
        return includeAddress;
    }

    @Override
    public Boolean getIncludeAppointmentDate() {
        return includeAppointmentDate;
    }

    @Override
    public Boolean getIncludeBasicInformation() {
        return includeBasicInformation;
    }

    @Override
    public Boolean getIncludeCountryOfResidence() {
        return includeCountryOfResidence;
    }

    @Override
    public IncludeDobTypeApi getIncludeDobType() {
        return includeDobTypeApi;
    }
}
