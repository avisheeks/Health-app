package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.model.User;

import java.util.List;

public interface PatientService {
    List<Patient> getAllPatients();
    
    Patient getPatientById(Long id);
    
    Patient getPatientByUser(User user);
    
    Patient getPatientByEmail(String email);
    
    Patient createPatient(User user);
    
    Patient updatePatient(Long id, Patient patientDetails);
    
    void deletePatient(Long id);
} 