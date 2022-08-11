package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.DeliveryDetailsApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class OrderNotificationEmailDataConverter implements OrderNotificationDataConvertable {

    private OrderNotificationEmailData emailData;
    private CertificateEmailDataMapper certificateEmailDataMapper;
    private CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper;
    private MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper;
    private EmailConfiguration emailConfiguration;

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
        emailData.setOrderSummaryLink("/GCI-2224/TODO");

        // delivery details
        mapDeliveryDetails(ordersApi);
        // payment details
        mapPaymentDetails(ordersApi);
    }

    @Override
    public void mapCertificate(BaseItemApi certificate) {
        List<Certificate> certificatesList;
        if (emailData.getCertificates() != null) {
            certificatesList = emailData.getCertificates();
            certificatesList.add(certificateEmailDataMapper.map(certificate));
        } else {
            certificatesList = new ArrayList<>();
            certificatesList.add(certificateEmailDataMapper.map(certificate));
        }
        emailData.setCertificates(certificatesList);
    }

    @Override
    public void mapCertifiedCopy(BaseItemApi certifiedCopy) {
        List<CertifiedCopy> certifiedCopyList;
        if (emailData.getCertifiedCopies() != null) {
            certifiedCopyList = emailData.getCertifiedCopies();
            certifiedCopyList.add(certifiedCopyEmailDataMapper.map(certifiedCopy));
        } else {
            certifiedCopyList = new ArrayList<>();
            certifiedCopyList.add(certifiedCopyEmailDataMapper.map(certifiedCopy));
        }
        emailData.setCertifiedCopies(certifiedCopyList);
    }

    @Override
    public void mapMissingImageDelivery(BaseItemApi missingImageDelivery) {
        List<MissingImageDelivery> missingImageDeliveryList;
        if (emailData.getMissingImageDeliveries() != null) {
            missingImageDeliveryList = emailData.getMissingImageDeliveries();
            missingImageDeliveryList.add(missingImageDeliveryEmailDataMapper
                    .map(missingImageDelivery));
        } else {
            missingImageDeliveryList = new ArrayList<>();
            missingImageDeliveryList.add(missingImageDeliveryEmailDataMapper
                    .map(missingImageDelivery));
        }
        emailData.setMissingImageDeliveries(missingImageDeliveryList);
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
                .withPostalCode((orderDeliveryDetails.getPostalCode()))
                .withRegion(orderDeliveryDetails.getRegion())
                .withForename(orderDeliveryDetails.getForename())
                .withSurname(orderDeliveryDetails.getSurname())
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
}
