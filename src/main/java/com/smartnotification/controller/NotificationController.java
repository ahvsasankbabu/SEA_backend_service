package com.smartnotification.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartnotification.dto.NotificationResponse;
import com.smartnotification.entity.User;
import com.smartnotification.repository.UserRepository;
import com.smartnotification.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByPrimaryEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<NotificationResponse> getAll(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationService.getNotifications(user)
                .stream().map(NotificationResponse::from).toList();
    }

    @GetMapping("/important")
    public List<NotificationResponse> getImportant(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationService.getImportantNotifications(user)
                .stream().map(NotificationResponse::from).toList();
    }

    @GetMapping("/ignored")
    public List<NotificationResponse> getIgnored(Authentication authentication) {
        User user = currentUser(authentication);
        return notificationService.getIgnoredNotifications(user)
                .stream().map(NotificationResponse::from).toList();
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, Authentication authentication) {
        notificationService.markAsRead(id, currentUser(authentication));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        notificationService.delete(id, currentUser(authentication));
    }
}