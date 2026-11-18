package com.hotel.soa.customer.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RegisterCustomerResponse", namespace = "http://hotel.com/soap/customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterCustomerResponse {

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private Long customerId;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String status;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String message;

    public RegisterCustomerResponse() {}

    public RegisterCustomerResponse(Long customerId, String status, String message) {
        this.customerId = customerId;
        this.status = status;
        this.message = message;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
