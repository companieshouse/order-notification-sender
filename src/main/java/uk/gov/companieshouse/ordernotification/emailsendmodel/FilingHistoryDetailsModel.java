package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class FilingHistoryDetailsModel {

    private String filingHistoryDate;
    private String filingHistoryDescription;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilingHistoryDetailsModel that = (FilingHistoryDetailsModel) o;
        return Objects.equals(filingHistoryDate, that.filingHistoryDate) && Objects.equals(filingHistoryDescription, that.filingHistoryDescription) && Objects.equals(filingHistoryType, that.filingHistoryType) && Objects.equals(filingHistoryCost, that.filingHistoryCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filingHistoryDate, filingHistoryDescription, filingHistoryType, filingHistoryCost);
    }
}
