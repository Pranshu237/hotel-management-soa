package com.hotel.soa.payment.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetPaymentDetailsRequest", namespace = "http://hotel.com/soap/payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPaymentDetailsRequest {

    @XmlElement(namespace = "http://hotel.com/soap/payment", required = true)
    private String transactionId;

    public GetPaymentDetailsRequest() {}

    public GetPaymentDetailsRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
