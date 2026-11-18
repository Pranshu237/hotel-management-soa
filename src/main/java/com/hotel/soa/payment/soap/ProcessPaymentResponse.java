package com.hotel.soa.payment.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProcessPaymentResponse", namespace = "http://hotel.com/soap/payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessPaymentResponse {

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String transactionId;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String status;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String message;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String receiptNumber;

    public ProcessPaymentResponse() {}

    public ProcessPaymentResponse(String transactionId, String status, String message, String receiptNumber) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
        this.receiptNumber = receiptNumber;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}
