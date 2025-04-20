package com.vansh.healthapp.controller;


import com.vansh.healthapp.model.Notification;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.payload.request.NotificationDTO;
import com.vansh.healthapp.service.NotificationService;
import com.vansh.healthapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationController(
            NotificationService notificationService,
            UserService userService,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getUserNotifications() {
        try {
            User currentUser = getCurrentUser();
            List<Notification> notifications = notificationService.getUserNotifications(currentUser);
            long unreadCount = notifications.stream()
                    .filter(n -> !n.isRead())
                    .count();

            // Create response object matching frontend expectations
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", convertToDTOs(notifications));
            response.put("unreadCount", unreadCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HashMap<String, String>() {{
                        put("message", "Error fetching notifications: " + e.getMessage());
                    }});
        }
    }

    @GetMapping("/health-alerts")
    public ResponseEntity<?> getHealthAlerts() {
        try {
            User currentUser = getCurrentUser();
            List<Notification> healthAlerts = notificationService.getUserNotificationsByType(
                    currentUser, "HEALTH_ALERT"
            );

            return ResponseEntity.ok(convertToDTOs(healthAlerts));
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch health alerts"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDTO>> getAllUserNotifications() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationService.getAllUserNotifications(currentUser);
        return ResponseEntity.ok(convertToDTOs(notifications));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationDTO>> getUserNotificationsByType(@PathVariable String type) {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationService.getUserNotificationsByType(currentUser, type);
        return ResponseEntity.ok(convertToDTOs(notifications));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(convertToDTO(notification));
    }

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public NotificationDTO sendNotification(Notification notification) {
        return convertToDTO(notification);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

    private List<NotificationDTO> convertToDTOs(List<Notification> notifications) {
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }

    @PostMapping("/test-notifications")
    public ResponseEntity<?> createTestNotifications() {
        try {
            User currentUser = getCurrentUser();

            // Create different types of notifications
            Notification healthAlert = notificationService.createNotification(
                    currentUser,
                    "Test Health Alert",
                    "This is a test health alert.",
                    "HEALTH_ALERT"
            );

            Notification medicationReminder = notificationService.createNotification(
                    currentUser,
                    "Test Medication Reminder",
                    "This is a test medication reminder.",
                    "MEDICATION_REMINDER"
            );

            Notification recoveryInsight = notificationService.createNotification(
                    currentUser,
                    "Test Recovery Insight",
                    "This is a test recovery insight.",
                    "RECOVERY_INSIGHT"
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Test notifications created successfully",
                    "notifications", List.of(healthAlert, medicationReminder, recoveryInsight)
            ));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create test notifications: " + e.getMessage()));
        }
    }



}
