package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data_raw")
public class SensorDataRaw {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Double heartRate;
    
    private Double bloodPressureSystolic;
    
    private Double bloodPressureDiastolic;
    
    private Double oxygenLevel;
    
    private Double temperature;
    
    private Integer stepCount;
    
    private LocalDateTime timestamp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @OneToOne(mappedBy = "sensorDataRaw", cascade = CascadeType.ALL, orphanRemoval = true)
    private HealthMetric healthMetric;
    
    public SensorDataRaw() {
    }
    
    public SensorDataRaw(Long id, Double heartRate, Double bloodPressureSystolic, Double bloodPressureDiastolic, 
                         Double oxygenLevel, Double temperature, Integer stepCount, LocalDateTime timestamp, Device device) {
        this.id = id;
        this.heartRate = heartRate;
        this.bloodPressureSystolic = bloodPressureSystolic;
        this.bloodPressureDiastolic = bloodPressureDiastolic;
        this.oxygenLevel = oxygenLevel;
        this.temperature = temperature;
        this.stepCount = stepCount;
        this.timestamp = timestamp;
        this.device = device;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getHeartRate() {
        return heartRate;
    }
    
    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }
    
    public Double getBloodPressureSystolic() {
        return bloodPressureSystolic;
    }
    
    public void setBloodPressureSystolic(Double bloodPressureSystolic) {
        this.bloodPressureSystolic = bloodPressureSystolic;
    }
    
    public Double getBloodPressureDiastolic() {
        return bloodPressureDiastolic;
    }
    
    public void setBloodPressureDiastolic(Double bloodPressureDiastolic) {
        this.bloodPressureDiastolic = bloodPressureDiastolic;
    }
    
    public Double getOxygenLevel() {
        return oxygenLevel;
    }
    
    public void setOxygenLevel(Double oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Integer getStepCount() {
        return stepCount;
    }
    
    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    public HealthMetric getHealthMetric() {
        return healthMetric;
    }
    
    public void setHealthMetric(HealthMetric healthMetric) {
        this.healthMetric = healthMetric;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SensorDataRaw that = (SensorDataRaw) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 