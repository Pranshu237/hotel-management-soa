package com.hotel.soa.customer.soap;

import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.service.CustomerService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CustomerSoapEndpoint {

    private static final String NAMESPACE_URI = "http://hotel.com/soap/customer";

    private final CustomerService customerService;

    public CustomerSoapEndpoint(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegisterCustomerRequest")
    @ResponsePayload
    public RegisterCustomerResponse registerCustomer(@RequestPayload RegisterCustomerRequest request) {
        RegisterCustomerResponse response = new RegisterCustomerResponse();
        try {
            Customer customer = customerService.registerCustomer(
                    request.getName(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getAddress()
            );
            response.setCustomerId(customer.getId());
            response.setStatus("SUCCESS");
            response.setMessage("Customer registered successfully");
        } catch (Exception e) {
            response.setCustomerId(-1L);
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetCustomerDetailsRequest")
    @ResponsePayload
    public GetCustomerDetailsResponse getCustomerDetails(@RequestPayload GetCustomerDetailsRequest request) {
        GetCustomerDetailsResponse response = new GetCustomerDetailsResponse();
        try {
            Customer customer = customerService.getCustomerById(request.getCustomerId());
            response.setCustomerId(customer.getId());
            response.setName(customer.getName());
            response.setEmail(customer.getEmail());
            response.setPhone(customer.getPhone());
            response.setAddress(customer.getAddress());
            response.setStatus("SUCCESS");
            response.setMessage("Customer details retrieved successfully");
        } catch (Exception e) {
            response.setCustomerId(request.getCustomerId());
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
