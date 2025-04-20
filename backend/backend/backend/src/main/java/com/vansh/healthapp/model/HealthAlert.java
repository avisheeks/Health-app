package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "health_alerts")
public class HealthAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alert_type")
    private String alertType;
    
    @Column(name = "message_data", columnDefinition = "TEXT")
    private String messageData;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @OneToMany(mappedBy = "healthAlert", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Medication> medications = new HashSet<>();
    
    public HealthAlert() {
    }
    
    public HealthAlert(Long id, String alertType, String messageData, Patient patient) {
        this.id = id;
        this.alertType = alertType;
        this.messageData = messageData;
        this.patient = patient;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getMessageData() {
        return messageData;
    }
    
    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Set<Medication> getMedications() {
        return medications;
    }
    
    public void setMedications(Set<Medication> medications) {
        this.medications = medications;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        HealthAlert that = (HealthAlert) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 