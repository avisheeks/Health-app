package com.vansh.healthapp.controller;

import com.vansh.healthapp.model.HealthAlert;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.service.HealthAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-alerts")
public class HealthAlertController {

    private final HealthAlertService healthAlertService;

    public HealthAlertController(HealthAlertService healthAlertService) {
        this.healthAlertService = healthAlertService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<List<HealthAlert>> getAllHealthAlerts() {
        List<HealthAlert> healthAlerts = healthAlertService.getAllHealthAlerts();
        return ResponseEntity.ok(healthAlerts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessHealthAlert(#id)")
    public ResponseEntity<HealthAlert> getHealthAlertById(@PathVariable Long id) {
        HealthAlert healthAlert = healthAlertService.getHealthAlertById(id);
        return ResponseEntity.ok(healthAlert);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.isPatient(#patientId)")
    public ResponseEntity<List<HealthAlert>> getHealthAlertsByPatient(@PathVariable Long patientId) {
        List<HealthAlert> healthAlerts = healthAlertService.getHealthAlertsByPatient(patientId);
        return ResponseEntity.ok(healthAlerts);
    }

    @GetMapping("/patient/{patientId}/type/{alertType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.isPatient(#patientId)")
    public ResponseEntity<List<HealthAlert>> getHealthAlertsByPatientAndType(
            @PathVariable Long patientId,
            @PathVariable String alertType) {
        List<HealthAlert> healthAlerts = healthAlertService.getHealthAlertsByPatientAndType(patientId, alertType);
        return ResponseEntity.ok(healthAlerts);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<MessageResponse> deleteHealthAlert(@PathVariable Long id) {
        healthAlertService.deleteHealthAlert(id);
        return ResponseEntity.ok(new MessageResponse("Health alert deleted successfully"));
    }
} 