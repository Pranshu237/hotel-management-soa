package com.hotel.soa.notification.repository;

import com.hotel.soa.notification.entity.Notification;
import com.hotel.soa.notification.entity.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Notification> findByCustomerIdAndIsReadFalseOrderByCreatedAtDesc(Long customerId);

    List<Notification> findByCustomerIdAndType(Long customerId, NotificationType type);

    long countByCustomerIdAndIsReadFalse(Long customerId);
}
