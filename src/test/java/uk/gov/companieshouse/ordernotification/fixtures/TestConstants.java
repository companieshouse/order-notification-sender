package uk.gov.companieshouse.ordernotification.fixtures;

import java.time.LocalDateTime;

public final class TestConstants {

    //common constants
    public static final String ORDER_REFERENCE_NUMBER = "87654321";
    public static final String COMPANY_NUMBER = "12345678";
    public static final String ORDER_COST = "15";
    public static final String ORDER_VIEW = "£15";
    public static final String PAYMENT_REFERENCE = "ABCD-EFGH-IJKL";
    public static final String ORDER_NOTIFICATION_REFERENCE = "/orders/" + ORDER_REFERENCE_NUMBER;
    public static final String KAFKA_TOPIC = "topic";
    public static final String PAYMENT_TIME = "27 July 2021 - 15:20:10";
    public static final String MAPPED_STANDARD_DELIVERY_TEXT = "Standard";
    public static final String MAPPED_EXPRESS_DELIVERY_TEXT = "Express";

    //certificates
    public static final String CERTIFICATE_ID = "CRT-123456-123456";
    public static final String MAPPED_INCORPORATION_CERTIFICATE_TYPE = "Incorporation with all company name changes";
    public static final String MAPPED_DISSOLUTION_CERTIFICATE_TYPE = "Dissolution with all company name changes";

    //document order constants
    public static final String CERTIFIED_COPY_ID = "CCD-123456-123456";
    public static final String FILING_HISTORY_DATE = "2021-07-28";
    public static final String MAPPED_FILING_HISTORY_DATE = "28 Jul 2021";
    public static final String FILING_HISTORY_DESCRIPTION = "confirmation-statement-with-updates";
    public static final String MAPPED_FILING_HISTORY_DESCRIPTION = "Mapped filing history description";
    public static final String MADE_UP_DATE = "2017-05-20";
    public static final String FILING_HISTORY_TYPE = "CS01";
    public static final String EMAIL_DATE_FORMAT = "dd MMMM yyyy";
    public static final String PAYMENT_DATE_FORMAT = "dd MMMM yyyy - HH:mm:ss";

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
    public static final String FILING_HISTORY_EMAIL_DATE_FORMAT = "dd MMM yyyy";

    //delivery details
    public static final String FORENAME = "forename";
    public static final String SURNAME = "surname";
    public static final String COMPANY_NAME = "company name";
    public static final String ADDRESS_LINE_1 = "address line 1";
    public static final String ADDRESS_LINE_2 = "address line 2";
    public static final String PO_BOX = "po box";
    public static final String LOCALITY = "locality";
    public static final String REGION = "region";
    public static final String POSTAL_CODE = "postal code";
    public static final String COUNTRY = "country";

    public static final LocalDateTime TEST_DATE = LocalDateTime.of(2021, 7, 27, 15, 20, 10);

    public static final String CHS_URL = "https://find-and-update.company-information.service.gov.uk/";
    public static final String USER_EMAIL = "demo@ch.gov.uk";
    public static final String CONFIRMATION_MESSAGE_HEAD = "Confirmation of your order number ";
    public static final String CONFIRMATION_MESSAGE = CONFIRMATION_MESSAGE_HEAD + "{0}";

    private TestConstants(){
    }
}
