package com.vansh.healthapp.service;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    public Patient getPatientByUser(User user) {
        return patientRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found for user: " + user.getEmail()));
    }

    @Override
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
    }

    @Override
    @Transactional
    public Patient createPatient(User user) {
        Patient patient = new Patient();
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    @Override
    @Transactional
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient patient = getPatientById(id);
        // Update patient-specific fields if needed in the future
        return patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }
} 