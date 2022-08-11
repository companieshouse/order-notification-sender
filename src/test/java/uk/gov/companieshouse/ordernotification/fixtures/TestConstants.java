package uk.gov.companieshouse.ordernotification.fixtures;

import java.time.LocalDateTime;

public final class TestConstants {

    //common constants
    public static final String ORDER_REFERENCE_NUMBER = "87654321";
    public static final String COMPANY_NAME = "ACME LTD";
    public static final String COMPANY_NUMBER = "12345678";
    public static final String ORDER_COST = "15";
    public static final String ORDER_VIEW = "£15";
    public static final String PAYMENT_REFERENCE = "ABCD-EFGH-IJKL";
    public static final String ORDER_NOTIFICATION_REFERENCE = "/orders/" + ORDER_REFERENCE_NUMBER;
    public static final String KAFKA_TOPIC = "topic";
    public static final String EMAIL_DATA = "Message content";
    public static final String CREATED_AT = "2020-08-25T09:27:09.519+01:00";
    public static final String TOPIC = "email-send";
    public static final String ORDER_RECEIVED_TOPIC = "order-received";
    public static final String ORDER_RECEIVED_KEY = "order-received";
    public static final String ORDER_RECEIVED_TOPIC_RETRY = "order-received-notification-retry";
    public static final String ORDER_RECEIVED_TOPIC_ERROR = "order-received-notification-error";
    public static final String EMAIL_RECIPIENT = "user@companieshouse.gov.uk";
    public static final String PAYMENT_TIME = "27 July 2021 - 15:20:10";
    public static final String SENDER_EMAIL_ADDRESS = "noreply@companieshouse.gov.uk";
    public static final String ORDER_CREATED_AT = "27 July 2021";
    public static final String EMAIL_COPY_EXPRESS_ONLY = "Email only available for express delivery method";
    public static final String DELIVERY_TIMESCALE = "STANDARD";
    public static final String MAPPED_STANDARD_DELIVERY_TEXT = "Standard delivery (aim to "
            + "dispatch within 10 working days)";
    public static final String MAPPED_EXPRESS_DELIVERY_TEXT = "Express (Orders received before 11am "
            + "will be dispatched the same day. Orders received after 11am will be dispatched the next working day)";

    //certificate order constants
    public static final String LIMITED_COMPANY_TYPE = "ltd";
    public static final String CERTIFICATE_TYPE = "Incorporation";
    public static final String ADDRESS_TYPE = "current-previous-and-prior";
    public static final String DISSOLVED_STATUS = "dissolved";
    public static final String DOB_TYPE = "dob-type";
    public static final String APPOINTMENT_DATE = "27 Sep 2021";
    public static final String COUNTRY_OF_RESIDENCE = "UK";
    public static final String NATIONALITY = "British";
    public static final String OCCUPATION = "Blacksmith";

    public static final String MESSAGE_ID = "message_id";
    public static final String APPLICATION_ID = "application_id";
    public static final String MESSAGE_TYPE = "message_type";

    //document order constants
    public static final String CERTIFIED_COPY_ID = "CCD-123456-123456";
    public static final String FILING_HISTORY_DATE = "2021-07-28";
    public static final String FILING_HISTORY_DATE_VIEW = "28 July 2021";
    public static final String MAPPED_FILING_HISTORY_DATE = "28 Jul 2021";
    public static final String DELIVERY_METHOD = "Standard delivery (aim to dispatch within 5 working days)";
    public static final String FILING_HISTORY_DESCRIPTION = "confirmation-statement-with-updates";
    public static final String MAPPED_FILING_HISTORY_DESCRIPTION = "Mapped filing history description";
    public static final String MADE_UP_DATE = "2017-05-20";
    public static final String FILING_HISTORY_TYPE = "CS01";
    public static final String EMAIL_DATE_FORMAT = "dd MMMM yyyy";
    public static final String PAYMENT_DATE_FORMAT = "dd MMMM yyyy - HH:mm:ss";
    public static final String CONFIRMATION_MESSAGE = "Confirmation of your order number {0}";

    //missing image order constants
    public static final String MISSING_IMAGE_DELIVERY_ID = "MID-123456-123456";

    //filing history description constants
    public static final String DESCRIPTION_FILE = "filing-history-test.yaml";
    public static final String DESCRIPTION_KEY = "appoint-person-director-company-with-name-date";
    public static final String EXPECTED_DESCRIPTION = "Appointment of The Appointee as a director on 12 February 2010";
    public static final String DESCRIPTION_KEY_LEGACY = "legacy";
    public static final String EXPECTED_DESCRIPTION_LEGACY = "This is the description";
    public static final String DESCRIPTION_KEY_ARRAY = "capital-allotment-shares";
    public static final String EXPECTED_DESCRIPTION_ARRAY = "Statement of capital following an allotment of shares on 10 November 2019";
    public static final String DESCRIPTION_KEY_NULL = "incorporation-company";
    public static final String EXPECTED_DESCRIPTION_NULL = "Incorporation";
    public static final String INCORPORATION_CERTIFICATE_TYPE = "Incorporation";
    public static final String EXPECTED_ADDRESS_TYPE = "Address type";
    public static final String READABLE_TRUE = "Yes";
    public static final String READABLE_FALSE = "No";
    public static final int DISPATCH_DAYS = 5;
    public static final String FILING_HISTORY_EMAIL_DATE_FORMAT = "dd MMM yyyy";

    public static final String FORENAME = "forename";
    public static final String SURNAME = "surname";
    public static final String ADDRESS_LINE_1 = "address line 1";
    public static final String ADDRESS_LINE_2 = "address line 2";
    public static final String PO_BOX = "po box";
    public static final String LOCALITY = "locality";
    public static final String REGION = "region";
    public static final String POSTAL_CODE = "postal code";
    public static final String COUNTRY = "country";

    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2022, 07, 27, 15, 20, 10);

    private TestConstants(){
    }
}
