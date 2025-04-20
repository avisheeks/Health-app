package com.vansh.healthapp.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SensorDataRequest {
    
    @NotBlank(message = "Device serial number is required")
    private String deviceSerialNumber;
    
    private Double heartRate;
    
    private Double bloodPressureSystolic;
    
    private Double bloodPressureDiastolic;
    
    private Double oxygenLevel;
    
    private Double temperature;
    
    private Integer stepCount;

    public SensorDataRequest() {
    }

    public SensorDataRequest(String deviceSerialNumber, Double heartRate, Double bloodPressureSystolic,
                             Double bloodPressureDiastolic, Double oxygenLevel, Double temperature, Integer stepCount) {
        this.deviceSerialNumber = deviceSerialNumber;
        this.heartRate = heartRate;
        this.bloodPressureSystolic = bloodPressureSystolic;
        this.bloodPressureDiastolic = bloodPressureDiastolic;
        this.oxygenLevel = oxygenLevel;
        this.temperature = temperature;
        this.stepCount = stepCount;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
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
} 