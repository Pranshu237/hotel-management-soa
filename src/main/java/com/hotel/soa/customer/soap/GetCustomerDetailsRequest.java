package com.hotel.soa.customer.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetCustomerDetailsRequest", namespace = "http://hotel.com/soap/customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerDetailsRequest {

    @XmlElement(namespace = "http://hotel.com/soap/customer", required = true)
    private Long customerId;

    public GetCustomerDetailsRequest() {}

    public GetCustomerDetailsRequest(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}
