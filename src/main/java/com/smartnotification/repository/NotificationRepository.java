package com.smartnotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.Notification;
import com.smartnotification.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByReceivedAtDesc(User user);

    List<Notification> findByUserAndNotifyTrueOrderByReceivedAtDesc(User user);

    List<Notification> findByUserAndNotifyFalseOrderByReceivedAtDesc(User user);

    Optional<Notification> findByIdAndUser(Long id, User user);

    Optional<Notification> findByGmailMessageId(String gmailMessageId);

    boolean existsByGmailMessageId(String gmailMessageId);
    void deleteByReadTrueAndReceivedAtBefore(java.time.LocalDateTime cutoff);
}