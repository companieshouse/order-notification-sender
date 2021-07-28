package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;
import java.util.Objects;

public class DocumentOrderNotificationModel extends OrderModel {

    private String deliveryMethod;
    private List<DocumentOrderDocumentDetailsModel> filingHistoryDocuments;

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public List<DocumentOrderDocumentDetailsModel> getFilingHistoryDocuments() {
        return filingHistoryDocuments;
    }

    public void setFilingHistoryDocuments(List<DocumentOrderDocumentDetailsModel> filingHistoryDocuments) {
        this.filingHistoryDocuments = filingHistoryDocuments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DocumentOrderNotificationModel that = (DocumentOrderNotificationModel) o;
        return Objects.equals(deliveryMethod, that.deliveryMethod) && Objects.equals(filingHistoryDocuments, that.filingHistoryDocuments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deliveryMethod, filingHistoryDocuments);
    }
}
