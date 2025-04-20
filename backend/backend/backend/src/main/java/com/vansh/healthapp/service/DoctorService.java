package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.User;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    
    Doctor getDoctorById(Long id);
    
    Doctor getDoctorByUser(User user);
    
    Doctor getDoctorByEmail(String email);
    
    Doctor createDoctor(User user);
    
    Doctor updateDoctor(Long id, Doctor doctorDetails);
    
    void deleteDoctor(Long id);
} 