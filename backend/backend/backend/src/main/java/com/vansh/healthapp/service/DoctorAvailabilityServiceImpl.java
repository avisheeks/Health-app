package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.DoctorAvailability;
import com.vansh.healthapp.payload.request.DoctorAvailabilityRequest;
import com.vansh.healthapp.repository.DoctorAvailabilityRepository;
import com.vansh.healthapp.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorAvailabilityServiceImpl(
            DoctorAvailabilityRepository availabilityRepository,
            DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public DoctorAvailability createAvailability(DoctorAvailabilityRequest request) {
        DoctorAvailability availability = new DoctorAvailability();
        // Set properties from request
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setDate(request.getDate());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setAvailable(request.isAvailable());
        if (!request.isAvailable()) {
            availability.setUnavailabilityReason(request.getUnavailabilityReason());
        }
        return availabilityRepository.save(availability);
    }

    @Override
    public List<DoctorAvailability> createBulkAvailability(DoctorAvailabilityRequest request) {
        List<DoctorAvailability> availabilities = new ArrayList<>();
        // Implement bulk creation logic
        return availabilities;
    }

    @Override
    public DoctorAvailability getAvailabilityById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor availability not found"));
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailability(Doctor doctor) {
        return availabilityRepository.findByDoctor(doctor);
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailabilityForDate(Doctor doctor, LocalDate date) {
        return availabilityRepository.findByDoctorAndDate(doctor, date);
    }

    @Override
    public List<DoctorAvailability> getDoctorAvailabilityForDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.findByDoctorAndDateBetween(doctor, startDate, endDate);
    }

    @Override
    public DoctorAvailability updateAvailability(Long id, DoctorAvailabilityRequest request) {
        DoctorAvailability availability = getAvailabilityById(id);
        // Update properties from request
        if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        if (request.getDate() != null) availability.setDate(request.getDate());
        availability.setAvailable(request.isAvailable());
        if (!request.isAvailable()) {
            availability.setUnavailabilityReason(request.getUnavailabilityReason());
        }
        return availabilityRepository.save(availability);
    }

    @Override
    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }

    @Override
    public void markDoctorUnavailable(Long doctorId, LocalDate date, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorAndDate(doctor, date);
        for (DoctorAvailability availability : availabilities) {
            availability.setAvailable(false);
            availability.setUnavailabilityReason(reason);
            availabilityRepository.save(availability);
        }
    }

    @Override
    public boolean isDoctorAvailable(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorAndDate(doctor, date);
        for (DoctorAvailability availability : availabilities) {
            if (availability.isAvailable() && 
                    !startTime.isBefore(availability.getStartTime()) && 
                    !endTime.isAfter(availability.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getAvailableSlots(Long doctorId, LocalDate date) {
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorAndDate(doctor, date);
        for (DoctorAvailability availability : availabilities) {
            if (availability.isAvailable()) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("id", availability.getId());
                slot.put("startTime", availability.getStartTime());
                slot.put("endTime", availability.getEndTime());
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    @Override
    public List<Map<String, Object>> getAvailableDoctors(String specialty, LocalDate date) {
        List<Map<String, Object>> availableDoctors = new ArrayList<>();
        // Implementation to find available doctors based on specialty and date
        return availableDoctors;
    }
} 