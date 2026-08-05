package com.smartnotification.service;

import java.util.List;

import com.smartnotification.dto.EmailDto;
import com.smartnotification.entity.ConnectedAccount;
import com.smartnotification.entity.Notification;
import com.smartnotification.entity.User;

public interface NotificationService {
    Notification saveNotification(EmailDto emailDto, User user, ConnectedAccount connectedAccount);
    List<Notification> getNotifications(User user);
    List<Notification> getImportantNotifications(User user);
    List<Notification> getIgnoredNotifications(User user);
    void markAsRead(Long id, User user);
    void delete(Long id, User user);
}