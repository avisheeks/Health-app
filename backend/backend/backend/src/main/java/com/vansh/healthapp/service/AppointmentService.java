package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Appointment;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.payload.request.AppointmentRequest;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    Appointment createAppointment(AppointmentRequest appointmentRequest);
    
    Appointment getAppointmentById(Long id);
    
    Appointment getAppointmentByNumber(String appointmentNumber);
    
    List<Appointment> getPatientAppointments(Patient patient);
    
    List<Appointment> getDoctorAppointments(Doctor doctor);
    
    List<Appointment> getPatientAppointmentsByStatus(Patient patient, Appointment.AppointmentStatus status);
    
    List<Appointment> getDoctorAppointmentsByStatus(Doctor doctor, Appointment.AppointmentStatus status);
    
    Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status);
    
    Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status, String cancellationReason);
    
    Appointment rescheduleAppointment(Long id, LocalDate newDate, String newStartTime, String newEndTime);
    
    Appointment addAppointmentNotes(Long id, String notes);
    
    void sendAppointmentReminder(Long appointmentId);
    
    void markAppointmentAsComplete(Long appointmentId);
    
    void markAppointmentAsNoShow(Long appointmentId);
    
    boolean checkDoctorAvailability(Long doctorId, LocalDate date, String startTime, String endTime);
    
    List<Appointment> getAppointmentsForDate(LocalDate date);
    
    List<Appointment> getDoctorAppointmentsForDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate);
    
    List<Appointment> getPatientAppointmentsForDateRange(Patient patient, LocalDate startDate, LocalDate endDate);
} 