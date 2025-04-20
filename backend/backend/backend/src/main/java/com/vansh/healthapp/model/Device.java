package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "devices")
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;
    
    private String model;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SensorDataRaw> sensorData = new HashSet<>();
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HealthMetric> healthMetrics = new HashSet<>();
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeviceLog> deviceLogs = new HashSet<>();
    
    public Device() {
    }
    
    public Device(Long id, String serialNumber, String model, LocalDateTime assignedAt, Patient patient) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.model = model;
        this.assignedAt = assignedAt;
        this.patient = patient;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Set<SensorDataRaw> getSensorData() {
        return sensorData;
    }
    
    public void setSensorData(Set<SensorDataRaw> sensorData) {
        this.sensorData = sensorData;
    }
    
    public Set<HealthMetric> getHealthMetrics() {
        return healthMetrics;
    }
    
    public void setHealthMetrics(Set<HealthMetric> healthMetrics) {
        this.healthMetrics = healthMetrics;
    }
    
    public Set<DeviceLog> getDeviceLogs() {
        return deviceLogs;
    }
    
    public void setDeviceLogs(Set<DeviceLog> deviceLogs) {
        this.deviceLogs = deviceLogs;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Device device = (Device) o;
        
        return id != null ? id.equals(device.id) : device.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 