package com.vansh.healthapp.service.impl;

import com.vansh.healthapp.exception.AppointmentException;
import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Appointment;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.payload.request.AppointmentRequest;
import com.vansh.healthapp.repository.AppointmentRepository;
import com.vansh.healthapp.repository.DoctorRepository;
import com.vansh.healthapp.repository.PatientRepository;
import com.vansh.healthapp.service.AppointmentService;
import com.vansh.healthapp.service.DoctorAvailabilityService;
import com.vansh.healthapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorAvailabilityService doctorAvailabilityService;
    private final NotificationService notificationService;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorAvailabilityService doctorAvailabilityService,
            NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorAvailabilityService = doctorAvailabilityService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentRequest appointmentRequest) {
        // Validate date is not in the past
        if (appointmentRequest.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new AppointmentException(
                    "Cannot book appointment for a past date",
                    AppointmentException.PAST_DATE_SELECTED);
        }
        
        // Get doctor and patient
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        Patient patient = patientRepository.findById(appointmentRequest.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        // Check doctor availability
        LocalTime startTime = appointmentRequest.getStartTime();
        LocalTime endTime = appointmentRequest.getEndTime();
        
        boolean doctorAvailable = doctorAvailabilityService.isDoctorAvailable(
                doctor, 
                appointmentRequest.getAppointmentDate(), 
                startTime,
                endTime);
        
        if (!doctorAvailable) {
            throw new AppointmentException(
                    "Doctor is not available at the selected time",
                    AppointmentException.DOCTOR_UNAVAILABLE);
        }
        
        // Check for overlapping appointments
        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                doctor,
                appointmentRequest.getAppointmentDate(),
                startTime,
                endTime);
        
        if (!overlappingAppointments.isEmpty()) {
            throw new AppointmentException(
                    "The selected time slot overlaps with an existing appointment",
                    AppointmentException.OVERLAPPING_APPOINTMENT);
        }
        
        // Create new appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber("RQ" + generateUniqueNumber());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentRequest.getAppointmentDate());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setReason(appointmentRequest.getReason());
        appointment.setNotes(appointmentRequest.getNotes());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING_CONFIRMATION);
        appointment.setReminderSent(false);
        
        if (appointmentRequest.getAmount() != null) {
            appointment.setAmount(appointmentRequest.getAmount());
            appointment.setIsPaid(false);
        }
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send notification to doctor
        sendAppointmentNotification(doctor.getUser(), patient.getUser(), savedAppointment, "NEW_APPOINTMENT");
        
        return savedAppointment;
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    @Override
    public Appointment getAppointmentByNumber(String appointmentNumber) {
        return appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getPatientAppointments(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDescStartTimeDesc(patient);
    }

    @Override
    public List<Appointment> getDoctorAppointments(Doctor doctor) {
        return appointmentRepository.findByDoctorOrderByAppointmentDateDescStartTimeDesc(doctor);
    }

    @Override
    public List<Appointment> getPatientAppointmentsByStatus(Patient patient, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByPatientAndStatusOrderByAppointmentDateDescStartTimeDesc(patient, status);
    }

    @Override
    public List<Appointment> getDoctorAppointmentsByStatus(Doctor doctor, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByDoctorAndStatusOrderByAppointmentDateDescStartTimeDesc(doctor, status);
    }

    @Override
    @Transactional
    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        return updateAppointmentStatus(id, status, null);
    }

    @Override
    @Transactional
    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status, String cancellationReason) {
        Appointment appointment = getAppointmentById(id);
        
        // Validate status change
        validateStatusChange(appointment.getStatus(), status);
        
        appointment.setStatus(status);
        
        if (status == Appointment.AppointmentStatus.CANCELLED && cancellationReason != null) {
            appointment.setCancellationReason(cancellationReason);
        }
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send notification based on new status
        String notificationType;
        switch (status) {
            case CONFIRMED:
                notificationType = "APPOINTMENT_CONFIRMED";
                break;
            case CANCELLED:
                notificationType = "APPOINTMENT_CANCELLED";
                break;
            case COMPLETED:
                notificationType = "APPOINTMENT_COMPLETED";
                break;
            default:
                notificationType = "APPOINTMENT_STATUS_CHANGED";
        }
        
        sendAppointmentNotification(
                appointment.getPatient().getUser(),
                appointment.getDoctor().getUser(),
                updatedAppointment,
                notificationType);
        
        return updatedAppointment;
    }

    @Override
    @Transactional
    public Appointment rescheduleAppointment(Long id, LocalDate newDate, String newStartTimeStr, String newEndTimeStr) {
        Appointment appointment = getAppointmentById(id);
        
        // Only pending or confirmed appointments can be rescheduled
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING_CONFIRMATION &&
            appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new AppointmentException(
                    "Only pending or confirmed appointments can be rescheduled",
                    AppointmentException.INVALID_STATUS_CHANGE);
        }
        
        // Validate date is not in the past
        if (newDate.isBefore(LocalDate.now())) {
            throw new AppointmentException(
                    "Cannot reschedule appointment to a past date",
                    AppointmentException.PAST_DATE_SELECTED);
        }
        
        LocalTime newStartTime = LocalTime.parse(newStartTimeStr, TIME_FORMATTER);
        LocalTime newEndTime = LocalTime.parse(newEndTimeStr, TIME_FORMATTER);
        
        // Check doctor availability
        boolean doctorAvailable = doctorAvailabilityService.isDoctorAvailable(
                appointment.getDoctor(),
                newDate,
                newStartTime,
                newEndTime);
        
        if (!doctorAvailable) {
            throw new AppointmentException(
                    "Doctor is not available at the selected time",
                    AppointmentException.DOCTOR_UNAVAILABLE);
        }
        
        // Check for overlapping appointments (excluding this appointment)
        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                appointment.getDoctor(),
                newDate,
                newStartTime,
                newEndTime);
        
        // Remove current appointment from overlapping list if found
        overlappingAppointments.removeIf(a -> a.getId().equals(appointment.getId()));
        
        if (!overlappingAppointments.isEmpty()) {
            throw new AppointmentException(
                    "The selected time slot overlaps with an existing appointment",
                    AppointmentException.OVERLAPPING_APPOINTMENT);
        }
        
        // Update appointment
        appointment.setAppointmentDate(newDate);
        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send notification
        sendAppointmentNotification(
                appointment.getPatient().getUser(),
                appointment.getDoctor().getUser(),
                updatedAppointment,
                "APPOINTMENT_RESCHEDULED");
        
        return updatedAppointment;
    }

    @Override
    @Transactional
    public Appointment addAppointmentNotes(Long id, String notes) {
        Appointment appointment = getAppointmentById(id);
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void sendAppointmentReminder(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        
        // Only send reminders for confirmed appointments
        if (appointment.getStatus() == Appointment.AppointmentStatus.CONFIRMED) {
            sendAppointmentNotification(
                    appointment.getPatient().getUser(),
                    appointment.getDoctor().getUser(),
                    appointment,
                    "APPOINTMENT_REMINDER");
            
            appointment.setReminderSent(true);
            appointmentRepository.save(appointment);
        }
    }

    @Override
    @Transactional
    public void markAppointmentAsComplete(Long appointmentId) {
        updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.COMPLETED);
    }

    @Override
    @Transactional
    public void markAppointmentAsNoShow(Long appointmentId) {
        updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.NO_SHOW);
    }

    @Override
    public boolean checkDoctorAvailability(Long doctorId, LocalDate date, String startTimeStr, String endTimeStr) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
        
        return doctorAvailabilityService.isDoctorAvailable(doctor, date, startTime, endTime);
    }

    @Override
    public List<Appointment> getAppointmentsForDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDateAndStatusIn(
                date,
                List.of(Appointment.AppointmentStatus.CONFIRMED, Appointment.AppointmentStatus.PENDING_CONFIRMATION));
    }

    @Override
    public List<Appointment> getDoctorAppointmentsForDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByAppointmentDateBetweenAndDoctor(startDate, endDate, doctor);
    }

    @Override
    public List<Appointment> getPatientAppointmentsForDateRange(Patient patient, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByAppointmentDateBetweenAndPatient(startDate, endDate, patient);
    }
    
    // Helper methods
    
    private void validateStatusChange(Appointment.AppointmentStatus currentStatus, Appointment.AppointmentStatus newStatus) {
        switch (currentStatus) {
            case PENDING_CONFIRMATION:
                // Can be confirmed or cancelled
                if (newStatus != Appointment.AppointmentStatus.CONFIRMED && 
                    newStatus != Appointment.AppointmentStatus.CANCELLED) {
                    throw new AppointmentException(
                            "Invalid status change from " + currentStatus + " to " + newStatus,
                            AppointmentException.INVALID_STATUS_CHANGE);
                }
                break;
            case CONFIRMED:
                // Can be completed, cancelled, or no-show
                if (newStatus != Appointment.AppointmentStatus.COMPLETED && 
                    newStatus != Appointment.AppointmentStatus.CANCELLED &&
                    newStatus != Appointment.AppointmentStatus.NO_SHOW) {
                    throw new AppointmentException(
                            "Invalid status change from " + currentStatus + " to " + newStatus,
                            AppointmentException.INVALID_STATUS_CHANGE);
                }
                break;
            case CANCELLED:
            case COMPLETED:
            case NO_SHOW:
                // Terminal states - cannot be changed
                throw new AppointmentException(
                        "Cannot change appointment from " + currentStatus + " status",
                        AppointmentException.INVALID_STATUS_CHANGE);
            default:
                throw new AppointmentException(
                        "Unknown appointment status: " + currentStatus,
                        AppointmentException.INVALID_STATUS_CHANGE);
        }
    }
    
    private String generateUniqueNumber() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    private void sendAppointmentNotification(User recipient, User sender, Appointment appointment, String type) {
        String title;
        String message;
        
        switch (type) {
            case "NEW_APPOINTMENT":
                title = "New Appointment Request";
                message = String.format(
                        "New appointment request from %s %s on %s at %s",
                        sender.getFirstName(),
                        sender.getLastName(),
                        appointment.getAppointmentDate(),
                        appointment.getStartTime());
                break;
            case "APPOINTMENT_CONFIRMED":
                title = "Appointment Confirmed";
                message = String.format(
                        "Your appointment with Dr. %s %s on %s at %s has been confirmed",
                        sender.getFirstName(),
                        sender.getLastName(),
                        appointment.getAppointmentDate(),
                        appointment.getStartTime());
                break;
            case "APPOINTMENT_CANCELLED":
                title = "Appointment Cancelled";
                message = String.format(
                        "Your appointment on %s at %s has been cancelled",
                        appointment.getAppointmentDate(),
                        appointment.getStartTime());
                break;
            case "APPOINTMENT_RESCHEDULED":
                title = "Appointment Rescheduled";
                message = String.format(
                        "Your appointment has been rescheduled to %s at %s",
                        appointment.getAppointmentDate(),
                        appointment.getStartTime());
                break;
            case "APPOINTMENT_REMINDER":
                title = "Appointment Reminder";
                message = String.format(
                        "Reminder: You have an appointment tomorrow at %s",
                        appointment.getStartTime());
                break;
            default:
                title = "Appointment Update";
                message = String.format(
                        "Your appointment on %s at %s has been updated",
                        appointment.getAppointmentDate(),
                        appointment.getStartTime());
        }
        
        notificationService.sendNotification(
                recipient.getId(),
                sender.getId(),
                title,
                message,
                "APPOINTMENT",
                appointment.getId().toString());
    }
} 