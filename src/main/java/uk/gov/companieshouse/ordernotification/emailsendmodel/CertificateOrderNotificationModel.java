package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String certificateType;
    private String statementOfGoodStanding;
    private boolean renderStatementOfGoodStanding;
    private String deliveryMethod;
    private String registeredOfficeAddressDetails;
    private CertificateDetailsModel directorDetailsModel;
    private CertificateDetailsModel secretaryDetailsModel;
    private String companyObjects;
    private String companyType;
    private CertificateDetailsModel designatedMembersDetails;
    private CertificateDetailsModel membersDetails;
    private String generalPartnerDetails;
    private String limitedPartnerDetails;
    private String principalPlaceOfBusinessDetails;
    private String generalNatureOfBusinessInformation;
    private FeatureOptions featureOptions;
    private String liquidatorsDetails;
    private boolean renderLiquidatorsDetails;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getStatementOfGoodStanding() {
        return statementOfGoodStanding;
    }

    public void setStatementOfGoodStanding(String statementOfGoodStanding) {
        this.statementOfGoodStanding = statementOfGoodStanding;
    }

    public boolean isRenderStatementOfGoodStanding() {
        return renderStatementOfGoodStanding;
    }

    public void setRenderStatementOfGoodStanding(boolean renderStatementOfGoodStanding) {
        this.renderStatementOfGoodStanding = renderStatementOfGoodStanding;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public void setRegisteredOfficeAddressDetails(String registeredOfficeAddressDetails) {
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
    }

    public CertificateDetailsModel getDirectorDetailsModel() {
        return directorDetailsModel;
    }

    public void setDirectorDetailsModel(CertificateDetailsModel directorDetailsModel) {
        this.directorDetailsModel = directorDetailsModel;
    }

    public CertificateDetailsModel getSecretaryDetailsModel() {
        return secretaryDetailsModel;
    }

    public void setSecretaryDetailsModel(CertificateDetailsModel secretaryDetailsModel) {
        this.secretaryDetailsModel = secretaryDetailsModel;
    }

    public String getCompanyObjects() {
        return companyObjects;
    }

    public void setCompanyObjects(String companyObjects) {
        this.companyObjects = companyObjects;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public CertificateDetailsModel getDesignatedMembersDetails() {
        return designatedMembersDetails;
    }

    public void setDesignatedMembersDetails(CertificateDetailsModel designatedMembersDetails) {
        this.designatedMembersDetails = designatedMembersDetails;
    }

    public CertificateDetailsModel getMembersDetails() {
        return membersDetails;
    }

    public void setMembersDetails(CertificateDetailsModel membersDetails) {
        this.membersDetails = membersDetails;
    }

    public String getGeneralPartnerDetails() {
        return generalPartnerDetails;
    }

    public void setGeneralPartnerDetails(String generalPartnerDetails) {
        this.generalPartnerDetails = generalPartnerDetails;
    }

    public String getLimitedPartnerDetails() {
        return limitedPartnerDetails;
    }

    public void setLimitedPartnerDetails(String limitedPartnerDetails) {
        this.limitedPartnerDetails = limitedPartnerDetails;
    }

    public String getPrincipalPlaceOfBusinessDetails() {
        return principalPlaceOfBusinessDetails;
    }

    public void setPrincipalPlaceOfBusinessDetails(String principalPlaceOfBusinessDetails) {
        this.principalPlaceOfBusinessDetails = principalPlaceOfBusinessDetails;
    }

    public String getGeneralNatureOfBusinessInformation() {
        return generalNatureOfBusinessInformation;
    }

    public void setGeneralNatureOfBusinessInformation(String generalNatureOfBusinessInformation) {
        this.generalNatureOfBusinessInformation = generalNatureOfBusinessInformation;
    }

    public FeatureOptions getFeatureOptions() {
        return featureOptions;
    }

    public void setFeatureOptions(FeatureOptions featureOptions) {
        this.featureOptions = featureOptions;
    }

    public String getLiquidatorsDetails() {
        return liquidatorsDetails;
    }

    public void setLiquidatorsDetails(String liquidatorsDetails) {
        this.liquidatorsDetails = liquidatorsDetails;
    }

    public boolean isRenderLiquidatorsDetails() {
        return renderLiquidatorsDetails;
    }

    public void setRenderLiquidatorsDetails(boolean renderLiquidatorsDetails) {
        this.renderLiquidatorsDetails = renderLiquidatorsDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return Objects.equals(certificateType, that.certificateType) &&
                Objects.equals(statementOfGoodStanding, that.statementOfGoodStanding) &&
                Objects.equals(deliveryMethod, that.deliveryMethod) &&
                Objects.equals(registeredOfficeAddressDetails, that.registeredOfficeAddressDetails) &&
                Objects.equals(directorDetailsModel, that.directorDetailsModel) &&
                Objects.equals(secretaryDetailsModel, that.secretaryDetailsModel) &&
                Objects.equals(companyObjects, that.companyObjects) &&
                Objects.equals(companyType, that.companyType) &&
                Objects.equals(designatedMembersDetails, that.designatedMembersDetails) &&
                Objects.equals(membersDetails, that.membersDetails) &&
                Objects.equals(generalPartnerDetails, that.generalPartnerDetails) &&
                Objects.equals(limitedPartnerDetails, that.limitedPartnerDetails) &&
                Objects.equals(principalPlaceOfBusinessDetails, that.principalPlaceOfBusinessDetails) &&
                Objects.equals(generalNatureOfBusinessInformation, that.generalNatureOfBusinessInformation) &&
                Objects.equals(featureOptions, that.featureOptions) &&
                Objects.equals(liquidatorsDetails, that.liquidatorsDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), certificateType, statementOfGoodStanding, deliveryMethod,
                registeredOfficeAddressDetails, directorDetailsModel, secretaryDetailsModel, companyObjects,
                companyType, designatedMembersDetails, membersDetails, generalPartnerDetails, limitedPartnerDetails,
                principalPlaceOfBusinessDetails, generalNatureOfBusinessInformation, featureOptions, liquidatorsDetails);
    }
}
