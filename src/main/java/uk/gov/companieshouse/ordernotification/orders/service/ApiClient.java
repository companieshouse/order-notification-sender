package uk.gov.companieshouse.ordernotification.orders.service;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class ApiClient {

    public InternalApiClient getPrivateApiClient() {
        return ApiSdkManager.getPrivateSDK();
    }

    public InternalApiClient getInternalApiClient() {
        return ApiSdkManager.getInternalSDK();
    }

}
