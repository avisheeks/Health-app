package com.vansh.healthapp.controller;

import com.vansh.healthapp.exception.AppointmentException;
import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Appointment;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.payload.request.AppointmentRequest;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.repository.DoctorRepository;
import com.vansh.healthapp.repository.PatientRepository;
import com.vansh.healthapp.service.AppointmentService;
import com.vansh.healthapp.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorAvailabilityService doctorAvailabilityService;

    @Autowired
    public AppointmentController(
            AppointmentService appointmentService,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorAvailabilityService doctorAvailabilityService) {
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorAvailabilityService = doctorAvailabilityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest appointmentRequest) {
        try {
            Appointment appointment = appointmentService.createAppointment(appointmentRequest);
            return new ResponseEntity<>(appointment, HttpStatus.CREATED);
        } catch (AppointmentException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred while creating the appointment"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/number/{appointmentNumber}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getAppointmentByNumber(@PathVariable String appointmentNumber) {
        try {
            Appointment appointment = appointmentService.getAppointmentByNumber(appointmentNumber);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            
            List<Appointment> appointments = appointmentService.getPatientAppointments(patient);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctor);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}/status/{status}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getPatientAppointmentsByStatus(
            @PathVariable Long patientId,
            @PathVariable String status) {
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            
            List<Appointment> appointments = appointmentService.getPatientAppointmentsByStatus(patient, appointmentStatus);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new MessageResponse("Invalid appointment status: " + status),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/doctor/{doctorId}/status/{status}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getDoctorAppointmentsByStatus(
            @PathVariable Long doctorId,
            @PathVariable String status) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            
            List<Appointment> appointments = appointmentService.getDoctorAppointmentsByStatus(doctor, appointmentStatus);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new MessageResponse("Invalid appointment status: " + status),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String cancellationReason) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            
            Appointment appointment;
            if (cancellationReason != null && !cancellationReason.isEmpty()) {
                appointment = appointmentService.updateAppointmentStatus(id, appointmentStatus, cancellationReason);
            } else {
                appointment = appointmentService.updateAppointmentStatus(id, appointmentStatus);
            }
            
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new MessageResponse("Invalid appointment status: " + status),
                    HttpStatus.BAD_REQUEST);
        } catch (AppointmentException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @RequestParam String newStartTime,
            @RequestParam String newEndTime) {
        try {
            Appointment appointment = appointmentService.rescheduleAppointment(id, newDate, newStartTime, newEndTime);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (AppointmentException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("An error occurred while rescheduling the appointment: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/notes")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> addAppointmentNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        try {
            Appointment appointment = appointmentService.addAppointmentNotes(id, notes);
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/reminder")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> sendAppointmentReminder(@PathVariable Long id) {
        try {
            appointmentService.sendAppointmentReminder(id);
            return new ResponseEntity<>(
                    new MessageResponse("Reminder sent successfully"),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> markAppointmentAsComplete(@PathVariable Long id) {
        try {
            appointmentService.markAppointmentAsComplete(id);
            return new ResponseEntity<>(
                    new MessageResponse("Appointment marked as completed"),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (AppointmentException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/noshow")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> markAppointmentAsNoShow(@PathVariable Long id) {
        try {
            appointmentService.markAppointmentAsNoShow(id);
            return new ResponseEntity<>(
                    new MessageResponse("Appointment marked as no-show"),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (AppointmentException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    e.getStatus() != null ? e.getStatus() : HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/availability/check")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> checkDoctorAvailability(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            boolean isAvailable = appointmentService.checkDoctorAvailability(doctorId, date, startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("Error checking availability: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getAppointmentsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsForDate(date);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/range")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getDoctorAppointmentsForDateRange(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
            List<Appointment> appointments = appointmentService.getDoctorAppointmentsForDateRange(doctor, startDate, endDate);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}/range")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getPatientAppointmentsForDateRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            
            List<Appointment> appointments = appointmentService.getPatientAppointmentsForDateRange(patient, startDate, endDate);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<?> getDoctorAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Map<String, Object>> availableSlots = doctorAvailabilityService.getAvailableSlots(doctorId, date);
            return new ResponseEntity<>(availableSlots, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(
                    new MessageResponse(e.getMessage()),
                    HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new MessageResponse("Error retrieving available slots: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 