package com.vansh.healthapp.service;


import com.vansh.healthapp.model.Notification;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.payload.request.NotificationDTO;
import com.vansh.healthapp.repository.NotificationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final HealthAlertService healthAlertService;
    private final MedicationService medicationService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public NotificationService(
            NotificationRepository notificationRepository,
            HealthAlertService healthAlertService,
            MedicationService medicationService,
            UserService userService,
            SimpMessagingTemplate messagingTemplate, UserServiceImpl userServiceImpl) {
        this.notificationRepository = notificationRepository;
        this.healthAlertService = healthAlertService;
        this.medicationService = medicationService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.userServiceImpl = userServiceImpl;
    }

    public Notification createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification(title, message, type, user);
        Notification savedNotification = notificationRepository.save(notification);

        // Convert to DTO before sending
        NotificationDTO notificationDTO = new NotificationDTO(
                savedNotification.getId(),
                savedNotification.getTitle(),
                savedNotification.getMessage(),
                savedNotification.getType(),
                savedNotification.isRead(),
                savedNotification.getCreatedAt(),
                savedNotification.getReadAt()
        );

        // Send to specific user's queue
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                notificationDTO
        );

        return savedNotification;
    }

    // Method to send notification with userId, targetId, title, message, type and status
    public void sendNotification(Long userId, Long targetId, String title, String message, String type, String status) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                createNotification(user, title, message, type);
            }
        } catch (Exception e) {
            System.out.println("Failed to send notification: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkHealthAlerts() {
        List<User> patients = healthAlertService.getPatientsWithRecentMetrics();

        for (User patient : patients) {
            if (healthAlertService.hasCriticalMetrics(patient).isPresent()) {
                createNotification(
                        patient,
                        "Health Alert",
                        "Your health metrics require attention. Please check your dashboard.",
                        "HEALTH_ALERT"
                );
            }
        }
    }

    @Scheduled(fixedRate = 3600000) // Check every hour
    public void checkMedicationReminders() {
        List<User> patients = medicationService.getPatientsWithActiveMedications();

        for (User patient : patients) {
            if (medicationService.isMedicationDue(patient)) {
                createNotification(
                        patient,
                        "Medication Reminder",
                        "It's time to take your medication. Please update your medication details.",
                        "MEDICATION_REMINDER"
                );
            }
        }
    }

    @Scheduled(fixedRate = 86400000) // Check every day
    public void sendRecoveryInsights() {
        List<User> doctors = userService.getAllUsers().stream()
                .filter(user -> user.hasRole("ROLE_DOCTOR"))
                .toList();

        for (User doctor : doctors) {
            List<User> patients = medicationService.getPatientsWithCompletedCycles(doctor);

            for (User patient : patients) {
                createNotification(
                        doctor,
                        "Recovery Insight",
                        String.format("Patient %s has completed their medication cycle. Review their recovery progress.",
                                patient.getFirstName()),
                        "RECOVERY_INSIGHT"
                );
            }
        }
    }



    public List<Notification> getUserNotifications(User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        } catch (Exception e) {
//            logger.error("Error fetching notifications for user: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to fetch notifications", e);
        }
    }

    public List<Notification> getUserNotificationsByType(User user, String type) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            return notificationRepository.findByUserAndTypeAndIsReadFalseOrderByCreatedAtDesc(user, type);
        } catch (Exception e) {

            throw new RuntimeException("Failed to fetch notifications by type", e);
        }
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

//    @PostConstruct
//    public void init() {
//        // Add some test notifications if none exist
//        if (notificationRepository.count() == 0) {
//            User user = userService.getCurrentUser(); // Implement this method
//            createNotification(
//                    user,
//                    "High Blood Pressure Alert",
//                    "Your blood pressure reading is above normal.",
//                    "HEALTH_ALERT"
//            );
//            createNotification(
//                    user,
//                    "Medication Reminder",
//                    "Time to take your evening medication.",
//                    "MEDICATION_REMINDER"
//            );
//        }
//    }

    @PostConstruct
    public void init() {
        try {
            // Add some test notifications if none exist
            if (notificationRepository.count() == 0) {
                User currentUser = userService.getCurrentUser();
                createTestNotificationsForUser(currentUser);
            }
        } catch (Exception e) {
            System.out.println("Could not create test notifications: {}" + e.getMessage());
        }
    }



    public void createTestNotificationsForUser(User user) {
        // Create health alert
        createNotification(
                user,
                "High Blood Pressure Alert",
                "Your blood pressure reading is above normal.",
                "HEALTH_ALERT"
        );

        // Create medication reminder
        createNotification(
                user,
                "Medication Reminder",
                "Time to take your evening medication.",
                "MEDICATION_REMINDER"
        );

        // Create recovery insight
        createNotification(
                user,
                "Recovery Progress",
                "Your recovery is progressing well.",
                "RECOVERY_INSIGHT"
        );
    }





}
