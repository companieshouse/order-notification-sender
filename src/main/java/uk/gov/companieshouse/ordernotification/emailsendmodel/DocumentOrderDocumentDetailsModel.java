package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class DocumentOrderDocumentDetailsModel {

    private String filingHistoryDate;
    private String filingHistoryDescription;
    private String madeUpDate;
    private String filingHistoryType;
    private String filingHistoryCost;

    public String getFilingHistoryDate() {
        return filingHistoryDate;
    }

    public void setFilingHistoryDate(String filingHistoryDate) {
        this.filingHistoryDate = filingHistoryDate;
    }

    public String getFilingHistoryDescription() {
        return filingHistoryDescription;
    }

    public void setFilingHistoryDescription(String filingHistoryDescription) {
        this.filingHistoryDescription = filingHistoryDescription;
    }

    public String getMadeUpDate() {
        return madeUpDate;
    }

    public void setMadeUpDate(String madeUpDate) {
        this.madeUpDate = madeUpDate;
    }

    public String getFilingHistoryType() {
        return filingHistoryType;
    }

    public void setFilingHistoryType(String filingHistoryType) {
        this.filingHistoryType = filingHistoryType;
    }

    public String getFilingHistoryCost() {
        return filingHistoryCost;
    }

    public void setFilingHistoryCost(String filingHistoryCost) {
        this.filingHistoryCost = filingHistoryCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentOrderDocumentDetailsModel that = (DocumentOrderDocumentDetailsModel) o;
        return Objects.equals(filingHistoryDate, that.filingHistoryDate) && Objects.equals(filingHistoryDescription, that.filingHistoryDescription) && Objects.equals(madeUpDate, that.madeUpDate) && Objects.equals(filingHistoryType, that.filingHistoryType) && Objects.equals(filingHistoryCost, that.filingHistoryCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filingHistoryDate, filingHistoryDescription, madeUpDate, filingHistoryType, filingHistoryCost);
    }
}
