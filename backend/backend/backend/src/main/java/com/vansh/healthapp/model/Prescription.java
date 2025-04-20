package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "prescription_date")
    private LocalDateTime prescriptionDate;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    
    @OneToOne(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private HealthReport healthReport;
    
    public Prescription() {
    }
    
    public Prescription(Long id, LocalDateTime prescriptionDate, String notes, Appointment appointment) {
        this.id = id;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
        this.appointment = appointment;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(LocalDateTime prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Appointment getAppointment() {
        return appointment;
    }
    
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    
    public HealthReport getHealthReport() {
        return healthReport;
    }
    
    public void setHealthReport(HealthReport healthReport) {
        this.healthReport = healthReport;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Prescription that = (Prescription) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 