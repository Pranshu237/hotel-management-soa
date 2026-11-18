package com.hotel.soa.customer.repository;

import com.hotel.soa.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    List<Customer> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Customer> findByUserId(Long userId);
}
