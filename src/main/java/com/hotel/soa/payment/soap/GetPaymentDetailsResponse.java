package com.hotel.soa.payment.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetPaymentDetailsResponse", namespace = "http://hotel.com/soap/payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPaymentDetailsResponse {

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String transactionId;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private Long bookingId;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private Long customerId;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private Double amount;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String paymentMode;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String status;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String receiptNumber;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String paymentDate;

    @XmlElement(namespace = "http://hotel.com/soap/payment")
    private String message;

    public GetPaymentDetailsResponse() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
