package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "received_at")
    private java.time.LocalDateTime receivedAt;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();
    
    public Doctor() {
    }
    
    public Doctor(Long id, User user, String status, java.time.LocalDateTime receivedAt) {
        this.id = id;
        this.user = user;
        this.status = status;
        this.receivedAt = receivedAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public java.time.LocalDateTime getReceivedAt() {
        return receivedAt;
    }
    
    public void setReceivedAt(java.time.LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
    
    public Set<Appointment> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Doctor doctor = (Doctor) o;
        
        return id != null ? id.equals(doctor.id) : doctor.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 