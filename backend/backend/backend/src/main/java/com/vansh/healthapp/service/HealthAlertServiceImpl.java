package com.vansh.healthapp.service;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.HealthAlert;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.repository.HealthAlertRepository;
import com.vansh.healthapp.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HealthAlertServiceImpl implements HealthAlertService {

    private final HealthAlertRepository healthAlertRepository;
    private final PatientRepository patientRepository;

    public HealthAlertServiceImpl(HealthAlertRepository healthAlertRepository, PatientRepository patientRepository) {
        this.healthAlertRepository = healthAlertRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public List<HealthAlert> getAllHealthAlerts() {
        return healthAlertRepository.findAll();
    }

    @Override
    public HealthAlert getHealthAlertById(Long id) {
        return healthAlertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health alert not found with id: " + id));
    }

    @Override
    public List<HealthAlert> getHealthAlertsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        return healthAlertRepository.findByPatient(patient);
    }

    @Override
    public List<HealthAlert> getHealthAlertsByPatientAndType(Long patientId, String alertType) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        return healthAlertRepository.findByPatientAndAlertType(patient, alertType);
    }

    @Override
    @Transactional
    public void deleteHealthAlert(Long id) {
        HealthAlert healthAlert = getHealthAlertById(id);
        healthAlertRepository.delete(healthAlert);
    }

    @Override
    public List<User> getPatientsWithRecentMetrics() {
        return List.of();
    }

    @Override
    public Optional<User> hasCriticalMetrics(User user) {
        return Optional.empty();
    }
} 