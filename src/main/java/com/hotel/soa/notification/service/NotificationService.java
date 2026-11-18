package com.hotel.soa.notification.service;

import com.hotel.soa.customer.entity.Customer;
import com.hotel.soa.customer.repository.CustomerRepository;
import com.hotel.soa.notification.entity.Notification;
import com.hotel.soa.notification.entity.Notification.NotificationType;
import com.hotel.soa.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               CustomerRepository customerRepository) {
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Notification createNotification(Long customerId, NotificationType type, String message) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + customerId));

        Notification notification = Notification.builder()
                .customer(customer)
                .type(type)
                .message(message)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    public List<Notification> getByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}
