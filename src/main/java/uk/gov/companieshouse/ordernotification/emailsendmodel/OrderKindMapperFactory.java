package uk.gov.companieshouse.ordernotification.emailsendmodel;

public interface OrderKindMapperFactory {
    OrderKindMapper getInstance(String kind);
}
