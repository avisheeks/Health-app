package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Notification;
import com.vansh.healthapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, String type);
    List<Notification> findByUserAndTypeAndIsReadFalseOrderByCreatedAtDesc(User user, String type);
}
