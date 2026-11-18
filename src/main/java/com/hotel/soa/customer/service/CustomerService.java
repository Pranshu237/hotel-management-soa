package com.hotel.soa.customer.service;

import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer registerCustomer(String name, String email, String phone, String address) {
        customerRepository.findByEmail(email).ifPresent(c -> {
            throw new IllegalArgumentException("Customer with email " + email + " already exists");
        });

        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();

        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
