package com.vansh.healthapp.security;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.*;
import com.vansh.healthapp.repository.DeviceRepository;
import com.vansh.healthapp.repository.DoctorRepo;
import com.vansh.healthapp.repository.HealthAlertRepository;
import com.vansh.healthapp.repository.PatientRepository;
import com.vansh.healthapp.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity")
public class UserSecurity {
    
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepo doctorRepository;
    private final DeviceRepository deviceRepository;
    private final HealthAlertRepository healthAlertRepository;

    public UserSecurity(
            UserRepository userRepository, 
            PatientRepository patientRepository, 
            DoctorRepo doctorRepository,
            DeviceRepository deviceRepository,
            HealthAlertRepository healthAlertRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.deviceRepository = deviceRepository;
        this.healthAlertRepository = healthAlertRepository;
    }

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String currentUserEmail = authentication.getName();
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getEmail().equals(currentUserEmail);
        }
        
        return false;
    }
    
    public boolean isPatient(Long patientId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String currentUserEmail = authentication.getName();
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            return patient.getUser().getEmail().equals(currentUserEmail);
        }
        
        return false;
    }
    
    public boolean isDoctor(Long doctorId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String currentUserEmail = authentication.getName();
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            return doctor.getUser().getEmail().equals(currentUserEmail);
        }
        
        return false;
    }
    
    public boolean canAccessDevice(Long deviceId) {
        // Get the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByFirstName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if the user is a patient and if the device belongs to them
        if (user.hasRole("PATIENT")) {
            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "user_id", user.getId().toString()));
            
            Device device = deviceRepository.findById(deviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Device", "id", deviceId.toString()));
            
            return device.getPatient().getId().equals(patient.getId());
        }
        
        return false;
    }

    public boolean canAccessHealthAlert(Long alertId) {
        // Get the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByFirstName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if the user is a patient and if the health alert belongs to them
        if (user.hasRole("PATIENT")) {
            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "user_id", user.getId().toString()));
            
            HealthAlert alert = healthAlertRepository.findById(alertId)
                    .orElseThrow(() -> new ResourceNotFoundException("HealthAlert", "id", alertId.toString()));
            
            return alert.getPatient().getId().equals(patient.getId());
        }
        
        return false;
    }
} 