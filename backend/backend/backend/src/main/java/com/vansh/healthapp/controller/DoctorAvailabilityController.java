package com.vansh.healthapp.controller;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.DoctorAvailability;
import com.vansh.healthapp.payload.request.DoctorAvailabilityRequest;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.repository.DoctorRepository;
import com.vansh.healthapp.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorAvailabilityController(
            DoctorAvailabilityService doctorAvailabilityService,
            DoctorRepository doctorRepository) {
        this.doctorAvailabilityService = doctorAvailabilityService;
        this.doctorRepository = doctorRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createAvailability(@Valid @RequestBody DoctorAvailabilityRequest request) {
        try {
            DoctorAvailability availability = doctorAvailabilityService.createAvailability(request);
            return new ResponseEntity<>(availability, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred while creating availability: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createBulkAvailability(@Valid @RequestBody DoctorAvailabilityRequest request) {
        try {
            List<DoctorAvailability> availabilities = doctorAvailabilityService.createBulkAvailability(request);
            return new ResponseEntity<>(availabilities, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred while creating bulk availability: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAvailabilityById(@PathVariable Long id) {
        try {
            DoctorAvailability availability = doctorAvailabilityService.getAvailabilityById(id);
            return new ResponseEntity<>(availability, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailability(doctor);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<?> getDoctorAvailabilityForDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilityForDate(doctor, date);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}/range")
    public ResponseEntity<?> getDoctorAvailabilityForDateRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            List<DoctorAvailability> availabilities = doctorAvailabilityService.getDoctorAvailabilityForDateRange(
                    doctor, startDate, endDate);
            return new ResponseEntity<>(availabilities, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody DoctorAvailabilityRequest request) {
        try {
            DoctorAvailability availability = doctorAvailabilityService.updateAvailability(id, request);
            return new ResponseEntity<>(availability, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred while updating availability: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> deleteAvailability(@PathVariable Long id) {
        try {
            doctorAvailabilityService.deleteAvailability(id);
            return new ResponseEntity<>(
                    new MessageResponse("Availability deleted successfully"),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/doctor/{doctorId}/unavailable")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> markDoctorUnavailable(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String reason) {
        try {
            doctorAvailabilityService.markDoctorUnavailable(doctorId, date, reason);
            return new ResponseEntity<>(
                    new MessageResponse("Doctor marked as unavailable for the selected date"),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<?> getAvailableDoctorsBySpecialty(
            @PathVariable String specialty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Map<String, Object>> availableDoctors = doctorAvailabilityService.getAvailableDoctors(specialty, date);
            return new ResponseEntity<>(availableDoctors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 