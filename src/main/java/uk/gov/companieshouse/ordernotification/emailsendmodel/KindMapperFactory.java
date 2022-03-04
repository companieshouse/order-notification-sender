package uk.gov.companieshouse.ordernotification.emailsendmodel;

public interface KindMapperFactory {
    KindMapper getInstance(String kind);
}
