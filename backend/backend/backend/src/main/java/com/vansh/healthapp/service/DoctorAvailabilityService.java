package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.DoctorAvailability;
import com.vansh.healthapp.payload.request.DoctorAvailabilityRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface DoctorAvailabilityService {

    DoctorAvailability createAvailability(DoctorAvailabilityRequest request);
    
    List<DoctorAvailability> createBulkAvailability(DoctorAvailabilityRequest request);
    
    DoctorAvailability getAvailabilityById(Long id);
    
    List<DoctorAvailability> getDoctorAvailability(Doctor doctor);
    
    List<DoctorAvailability> getDoctorAvailabilityForDate(Doctor doctor, LocalDate date);
    
    List<DoctorAvailability> getDoctorAvailabilityForDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate);
    
    DoctorAvailability updateAvailability(Long id, DoctorAvailabilityRequest request);
    
    void deleteAvailability(Long id);
    
    void markDoctorUnavailable(Long doctorId, LocalDate date, String reason);
    
    boolean isDoctorAvailable(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    List<Map<String, Object>> getAvailableSlots(Long doctorId, LocalDate date);
    
    List<Map<String, Object>> getAvailableDoctors(String specialty, LocalDate date);
} 