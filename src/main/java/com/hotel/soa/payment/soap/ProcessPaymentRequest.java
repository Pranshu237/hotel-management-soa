package com.hotel.soa.payment.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProcessPaymentRequest", namespace = "http://hotel.com/soap/payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessPaymentRequest {

    @XmlElement(namespace = "http://hotel.com/soap/payment", required = true)
    private Long bookingId;

    @XmlElement(namespace = "http://hotel.com/soap/payment", required = true)
    private Long customerId;

    @XmlElement(namespace = "http://hotel.com/soap/payment", required = true)
    private Double amount;

    @XmlElement(namespace = "http://hotel.com/soap/payment", required = true)
    private String paymentMode;

    public ProcessPaymentRequest() {}

    public ProcessPaymentRequest(Long bookingId, Long customerId, Double amount, String paymentMode) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMode = paymentMode;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
}
