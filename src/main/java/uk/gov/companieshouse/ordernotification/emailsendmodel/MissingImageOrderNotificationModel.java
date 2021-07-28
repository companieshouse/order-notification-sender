package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class MissingImageOrderNotificationModel extends OrderModel {
    private FilingHistoryDetailsModel filingHistoryDetails;

    public FilingHistoryDetailsModel getFilingHistoryDetails() {
        return filingHistoryDetails;
    }

    public void setFilingHistoryDetails(FilingHistoryDetailsModel filingHistoryDetails) {
        this.filingHistoryDetails = filingHistoryDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MissingImageOrderNotificationModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MissingImageOrderNotificationModel that = (MissingImageOrderNotificationModel) o;
        return Objects.equals(getFilingHistoryDetails(), that.getFilingHistoryDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFilingHistoryDetails());
    }
}
