package uk.gov.companieshouse.ordernotification.emailsendmodel;

public class ItemReadyNotificationEmailData extends OrderNotificationEmailData {

    private String orderNumber;
    private String itemId;
    private String digitalDocumentLocation;

    private String groupItem;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDigitalDocumentLocation() {
        return digitalDocumentLocation;
    }

    public void setDigitalDocumentLocation(String digitalDocumentLocation) {
        this.digitalDocumentLocation = digitalDocumentLocation;
    }

    public String getGroupItem() {
        return groupItem;
    }

    public void setGroupItem(String groupItem) {
        this.groupItem = groupItem;
    }
}
