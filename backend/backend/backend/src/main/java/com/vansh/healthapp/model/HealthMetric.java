package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_metrics")
public class HealthMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type;
    
    private Double value;
    
    private LocalDateTime timestamp;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_data_id")
    private SensorDataRaw sensorDataRaw;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    public HealthMetric() {
    }
    
    public HealthMetric(Long id, String type, Double value, LocalDateTime timestamp, SensorDataRaw sensorDataRaw, Device device) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.sensorDataRaw = sensorDataRaw;
        this.device = device;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public SensorDataRaw getSensorDataRaw() {
        return sensorDataRaw;
    }
    
    public void setSensorDataRaw(SensorDataRaw sensorDataRaw) {
        this.sensorDataRaw = sensorDataRaw;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        HealthMetric that = (HealthMetric) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 