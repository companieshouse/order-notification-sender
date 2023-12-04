package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class ItemGroupProcessedSendEmailSenderTest {

    private static final String EXPECTED_LOG_MESSAGE =
        "processing item-group-processed-send message: "
            + "{\"order_number\": \"ORD-065216-517934\", "
            + "\"group_item\": "
            + "\"/item-groups/IG-437617-007343/items/CCD-768116-517990\", "
            + "\"item\": {"
            + "\"id\": \"CCD-768116-517990\", "
            + "\"status\": \"satisfied\", "
            + "\"digital_document_location\": "
            + "\"s3://document-signing-api.development.ch.gov.uk/docker/certified-copy/application-pdf\"}}";

    @Mock
    private Logger logger;

    @InjectMocks
    private ItemGroupProcessedSendEmailSender itemGroupProcessedSendEmailSender;

    @Test
    void testHandleMessageLogsMessage() {

        // given
        final Message<ItemGroupProcessedSend> message = new GenericMessage<>(ITEM_GROUP_PROCESSED_SEND);

         // when
        itemGroupProcessedSendEmailSender.handleMessage(message);

        // then
        verify(logger).info(eq(EXPECTED_LOG_MESSAGE), anyMap());
    }

}