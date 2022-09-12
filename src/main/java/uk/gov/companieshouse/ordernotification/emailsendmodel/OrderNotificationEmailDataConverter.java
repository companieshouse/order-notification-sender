package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import uk.gov.companieshouse.api.model.order.DeliveryDetailsApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

public class OrderNotificationEmailDataConverter implements OrderNotificationDataConvertable {

    private static final String ORDER_SUMMARY_LINK = "%s/orders/%s";

    private final OrderNotificationEmailData emailData;
    private final CertificateEmailDataMapper certificateEmailDataMapper;
    private final CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper;
    private final MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper;
    private final EmailConfiguration emailConfiguration;

    public OrderNotificationEmailDataConverter(OrderNotificationEmailData emailData,
            CertificateEmailDataMapper certificateEmailDataMapper,
            CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper,
            MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper,
            EmailConfiguration emailConfiguration) {
        this.emailData = emailData;
        this.certificateEmailDataMapper = certificateEmailDataMapper;
        this.certifiedCopyEmailDataMapper = certifiedCopyEmailDataMapper;
        this.missingImageDeliveryEmailDataMapper = missingImageDeliveryEmailDataMapper;
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public void mapOrder(OrdersApi ordersApi) {
        emailData.setOrderId(ordersApi.getReference());
        emailData.setOrderSummaryLink(
                String.format(ORDER_SUMMARY_LINK, emailConfiguration.getChsUrl(), ordersApi.getReference()));
        mapDeliveryDetails(ordersApi);
        mapPaymentDetails(ordersApi);
        emailData.setDispatchDays(emailConfiguration.getDispatchDays());
        emailData.setTo(ordersApi.getOrderedBy().getEmail());
        emailData.setSubject(MessageFormat.format(emailConfiguration.getConfirmationMessage(), ordersApi.getReference()));
    }

    @Override
    public void mapCertificate(BaseItemApi certificateApi) {
        emailData.addCertificate(certificateEmailDataMapper.map(certificateApi));
        DeliveryTimescaleApi deliveryTimescaleApi = ((CertificateItemOptionsApi) certificateApi.getItemOptions()).getDeliveryTimescale();
        mapDeliveryTimescale(deliveryTimescaleApi);
    }

    @Override
    public void mapCertifiedCopy(BaseItemApi certifiedCopyApi) {
        emailData.addCertifiedCopy(certifiedCopyEmailDataMapper.map(certifiedCopyApi));
        DeliveryTimescaleApi deliveryTimescaleApi = ((CertifiedCopyItemOptionsApi) certifiedCopyApi.getItemOptions()).getDeliveryTimescale();
        mapDeliveryTimescale(deliveryTimescaleApi);
    }

    @Override
    public void mapMissingImageDelivery(BaseItemApi missingImageDeliveryApi) {
        emailData.addMissingImageDelivery(missingImageDeliveryEmailDataMapper.map(missingImageDeliveryApi));
    }

    @Override
    public OrderNotificationEmailData getEmailData() {
        return emailData;
    }

    private void mapDeliveryDetails(OrdersApi ordersApi) {
        DeliveryDetailsApi orderDeliveryDetails = ordersApi.getDeliveryDetails();
        emailData.setDeliveryDetails(DeliveryDetails.builder()
                .withAddressLine1(orderDeliveryDetails.getAddressLine1())
                .withAddressLine2(orderDeliveryDetails.getAddressLine2())
                .withCountry(orderDeliveryDetails.getCountry())
                .withLocality(orderDeliveryDetails.getLocality())
                .withPoBox(orderDeliveryDetails.getPoBox())
                .withPostalCode(orderDeliveryDetails.getPostalCode())
                .withRegion(orderDeliveryDetails.getRegion())
                .withForename(orderDeliveryDetails.getForename())
                .withSurname(orderDeliveryDetails.getSurname())
                .withCompanyName(orderDeliveryDetails.getCompanyName())
                .build());
    }

    private void mapPaymentDetails(OrdersApi ordersApi) {
        emailData.setPaymentDetails(PaymentDetails.builder()
            .withPaymentReference(ordersApi.getPaymentReference())
            .withAmountPaid("£" + ordersApi.getTotalOrderCost())
            .withPaymentDate(ordersApi.getOrderedAt().format(DateTimeFormatter.ofPattern(
                    emailConfiguration.getPaymentDateFormat())
            ))
            .build()
        );
    }

    private void mapDeliveryTimescale(DeliveryTimescaleApi deliveryTimescaleApi) {
        if (deliveryTimescaleApi == DeliveryTimescaleApi.STANDARD) {
            emailData.setHasStandardDelivery(true);
        } else if (deliveryTimescaleApi == DeliveryTimescaleApi.SAME_DAY){
            emailData.setHasExpressDelivery(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderNotificationEmailDataConverter that = (OrderNotificationEmailDataConverter) o;
        return Objects.equals(emailData, that.emailData) && Objects.equals(certificateEmailDataMapper, that.certificateEmailDataMapper) && Objects.equals(certifiedCopyEmailDataMapper, that.certifiedCopyEmailDataMapper) && Objects.equals(missingImageDeliveryEmailDataMapper, that.missingImageDeliveryEmailDataMapper) && Objects.equals(emailConfiguration, that.emailConfiguration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailData, certificateEmailDataMapper, certifiedCopyEmailDataMapper, missingImageDeliveryEmailDataMapper, emailConfiguration);
    }
}
