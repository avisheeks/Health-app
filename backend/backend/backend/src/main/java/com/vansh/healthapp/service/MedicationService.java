package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Medication;
import com.vansh.healthapp.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MedicationService {
    List<Medication> getAllMedications();

    Medication getMedicationById(Long id);

    List<Medication> getMedicationsByPatient(Long patientId);

    Medication createMedication(Medication medication);

    Medication updateMedication(Long id, Medication medicationDetails);

    void deleteMedication(Long id);

    List<User> getPatientsWithActiveMedications();

    boolean isMedicationDue(User patient);

    List<User> getPatientsWithCompletedCycles(User doctor);
}
