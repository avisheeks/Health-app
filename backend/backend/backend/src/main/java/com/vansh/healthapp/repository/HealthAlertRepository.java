package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.HealthAlert;
import com.vansh.healthapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthAlertRepository extends JpaRepository<HealthAlert, Long> {
    List<HealthAlert> findByPatient(Patient patient);
    
    List<HealthAlert> findByPatientAndAlertType(Patient patient, String alertType);
} 