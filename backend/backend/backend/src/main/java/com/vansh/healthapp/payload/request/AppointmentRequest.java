package com.vansh.healthapp.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @NotNull(message = "Doctor ID cannot be null")
    private Long doctorId;
    
    @NotNull(message = "Patient ID cannot be null")
    private Long patientId;
    
    @NotNull(message = "Appointment date cannot be null")
    private LocalDate appointmentDate;
    
    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;
    
    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;
    
    @NotBlank(message = "Reason cannot be blank")
    @Size(max = 500, message = "Reason must be less than 500 characters")
    private String reason;
    
    private String notes;
    
    private Double amount;
    
    public AppointmentRequest() {
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
} 