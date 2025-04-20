package com.vansh.healthapp.payload.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorAvailabilityRequest {
    
    private Long doctorId;
    private Integer dayOfWeek;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private String unavailabilityReason;
    
    // Default constructor
    public DoctorAvailabilityRequest() {
    }
    
    // Constructor with parameters
    public DoctorAvailabilityRequest(Long doctorId, Integer dayOfWeek, LocalDate date, 
                                    LocalTime startTime, LocalTime endTime, 
                                    boolean available, String unavailabilityReason) {
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.unavailabilityReason = unavailabilityReason;
    }
    
    // Getters and Setters
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
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
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getUnavailabilityReason() {
        return unavailabilityReason;
    }
    
    public void setUnavailabilityReason(String unavailabilityReason) {
        this.unavailabilityReason = unavailabilityReason;
    }
} 