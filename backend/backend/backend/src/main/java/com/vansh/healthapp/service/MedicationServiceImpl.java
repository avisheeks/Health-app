package com.vansh.healthapp.service;


import com.vansh.healthapp.model.Medication;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.repository.MedicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final UserService userService;

    @Autowired
    public MedicationServiceImpl(MedicationRepository medicationRepository, UserService userService) {
        this.medicationRepository = medicationRepository;
        this.userService = userService;
    }

    @Override
    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    @Override
    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
    }

    @Override
    public List<Medication> getMedicationsByPatient(Long patientId) {
        return medicationRepository.findByPatientId(patientId);
    }

    @Override
    public Medication createMedication(Medication medication) {
        return medicationRepository.save(medication);
    }

    @Override
    public Medication updateMedication(Long id, Medication medicationDetails) {
        Medication medication = getMedicationById(id);
        medication.setName(medicationDetails.getName());
        medication.setDosage(medicationDetails.getDosage());
        medication.setFrequency(medicationDetails.getFrequency());
        medication.setStartDate(medicationDetails.getStartDate());
        medication.setEndDate(medicationDetails.getEndDate());
        medication.setStatus(medicationDetails.getStatus());
        return medicationRepository.save(medication);
    }

    @Override
    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }

    @Override
    public List<User> getPatientsWithActiveMedications() {
        return medicationRepository.findByStatus("ACTIVE")
                .stream()
                .map(Medication::getPatient)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isMedicationDue(User patient) {
        List<Medication> activeMedications = medicationRepository.findByPatientAndStatus(patient, "ACTIVE");
        LocalDateTime now = LocalDateTime.now();

        return activeMedications.stream()
                .anyMatch(medication -> {
                    LocalDateTime nextDose = medication.getLastDoseTime()
                            .plusHours(medication.getFrequency());
                    return now.isAfter(nextDose);
                });
    }

    @Override
    public List<User> getPatientsWithCompletedCycles(User doctor) {
        return medicationRepository.findByDoctorAndStatus(doctor, "COMPLETED")
                .stream()
                .map(Medication::getPatient)
                .distinct()
                .collect(Collectors.toList());
    }
}
