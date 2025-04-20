package com.vansh.healthapp.service;

import com.vansh.healthapp.model.HealthAlert;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.model.User;

import java.util.List;
import java.util.Optional;

public interface HealthAlertService {
    List<HealthAlert> getAllHealthAlerts();

    HealthAlert getHealthAlertById(Long id);

    List<HealthAlert> getHealthAlertsByPatient(Long patientId);

    List<HealthAlert> getHealthAlertsByPatientAndType(Long patientId, String alertType);

    void deleteHealthAlert(Long id);

    List<User> getPatientsWithRecentMetrics();

    Optional<User> hasCriticalMetrics(User user);
} 