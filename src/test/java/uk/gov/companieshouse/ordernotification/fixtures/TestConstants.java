package uk.gov.companieshouse.ordernotification.fixtures;

public final class TestConstants {

    //common constants
    public static final String ORDER_REFERENCE_NUMBER = "87654321";
    public static final String COMPANY_NAME = "ACME LTD";
    public static final String COMPANY_NUMBER = "12345678";
    public static final String ORDER_COST = "15";
    public static final String PAYMENT_REFERENCE = "ABCD-EFGH-IJKL";
    public static final String ORDER_NOTIFICATION_REFERENCE = "/order/" + ORDER_REFERENCE_NUMBER;
    public static final String KAFKA_TOPIC = "topic";

    //certificate order constants
    public static final String CERTIFICATE_TYPE = "incorporation";
    public static final String ADDRESS_TYPE = "current-previous-and-prior";
    public static final String DOB_TYPE = "full";

    public static final String MESSAGE_ID = "message_id";
    public static final String APPLICATION_ID = "application_id";
    public static final String MESSAGE_TYPE = "message_type";

    //document order constants
    public static final String FILING_HISTORY_DATE = "2021-07-28";
    public static final String DELIVERY_METHOD = "postal";
    public static final String FILING_HISTORY_DESCRIPTION = "confirmation-statement-with-updates";
    public static final String MAPPED_FILING_HISTORY_DESCRIPTION = "Mapped filing history description";
    public static final String MADE_UP_DATE = "2017-05-20";
    public static final String FILING_HISTORY_TYPE = "CS01";
    public static final String SENDER_EMAIL_ADDRESS = "noreply@companieshouse.gov.uk";
    public static final String PAYMENT_TIME = "27 July 2021 - 15:20:10";
    public static final String EMAIL_DATE_FORMAT = "dd MMMM yyyy";
    public static final String PAYMENT_DATE_FORMAT = "dd MMMM yyyy - HH:mm:ss";
    public static final String CONFIRMATION_MESSAGE = "Confirmation of your order number {0}";

    //missing image order constants

    private TestConstants(){
    }
}
