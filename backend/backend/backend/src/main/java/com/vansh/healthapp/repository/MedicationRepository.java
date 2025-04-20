package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Medication;
import com.vansh.healthapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByPatientId(Long patientId);
    List<Medication> findByStatus(String status);
    List<Medication> findByPatientAndStatus(User patient, String status);
    List<Medication> findByDoctorAndStatus(User doctor, String status);

}
