package com.smartnotification.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartnotification.dto.EmailDto;
import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.entity.Notification;
import com.smartnotification.entity.User;
import com.smartnotification.repository.NotificationRepository;
import com.smartnotification.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification saveNotification(EmailDto emailDto, User user, ConnectedAccount connectedAccount) {

        if (emailDto.getGmailMessageId() == null || emailDto.getGmailMessageId().isBlank()) {
            throw new IllegalArgumentException("EmailDto missing gmailMessageId");
        }

        var existing = notificationRepository.findByGmailMessageId(emailDto.getGmailMessageId());
        if (existing.isPresent()) {
            log.info("Notification already exists for gmailMessageId={}", emailDto.getGmailMessageId());
            return existing.get();
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setConnectedAccount(connectedAccount);
        notification.setGmailMessageId(emailDto.getGmailMessageId());
        notification.setSender(emailDto.getFrom());
        notification.setSubject(emailDto.getSubject());
        notification.setBody(emailDto.getBody());
        notification.setSummary(emailDto.getSummary());
        notification.setReason(emailDto.getReason());
        notification.setPriority(emailDto.getPriority());
        notification.setNotify(emailDto.isNotify());
        notification.setReceivedAt(LocalDateTime.now());
        notification.setRead(false);

        try {
            return notificationRepository.save(notification);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent insert for gmailMessageId={}", emailDto.getGmailMessageId());
            return notificationRepository.findByGmailMessageId(emailDto.getGmailMessageId())
                    .orElseThrow(() -> e);
        }
    }

    @Override
    public List<Notification> getNotifications(User user) {
        return notificationRepository.findByUserOrderByReceivedAtDesc(user);
    }

    @Override
    public List<Notification> getImportantNotifications(User user) {
        return notificationRepository.findByUserAndNotifyTrueOrderByReceivedAtDesc(user);
    }

    @Override
    public List<Notification> getIgnoredNotifications(User user) {
        return notificationRepository.findByUserAndNotifyFalseOrderByReceivedAtDesc(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void delete(Long id, User user) {
        Notification notification = notificationRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
    }

    // Runs daily at 3 AM - deletes read notifications older than 2 days.
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldReadNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(2);
        notificationRepository.deleteByReadTrueAndReceivedAtBefore(cutoff);
        log.info("Cleaned up read notifications older than {}", cutoff);
    }
}