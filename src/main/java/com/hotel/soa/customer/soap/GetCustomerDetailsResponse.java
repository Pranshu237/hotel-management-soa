package com.hotel.soa.customer.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetCustomerDetailsResponse", namespace = "http://hotel.com/soap/customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerDetailsResponse {

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private Long customerId;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String name;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String email;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String phone;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String address;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String status;

    @XmlElement(namespace = "http://hotel.com/soap/customer")
    private String message;

    public GetCustomerDetailsResponse() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
