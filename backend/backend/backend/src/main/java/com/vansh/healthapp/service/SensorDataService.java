package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.HealthMetric;
import com.vansh.healthapp.model.SensorDataRaw;
import com.vansh.healthapp.payload.request.SensorDataRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SensorDataService {
    SensorDataRaw saveSensorData(SensorDataRequest sensorDataRequest);
    
    List<SensorDataRaw> getSensorDataByDevice(Long deviceId);
    
    List<SensorDataRaw> getSensorDataByDeviceAndTimeRange(Long deviceId, LocalDateTime start, LocalDateTime end);
    
    SensorDataRaw getLatestSensorDataByDevice(Long deviceId);
    
    List<HealthMetric> getHealthMetricsByDevice(Long deviceId);
    
    List<HealthMetric> getHealthMetricsByDeviceAndType(Long deviceId, String type);
    
    List<HealthMetric> getHealthMetricsByDeviceAndTimeRange(Long deviceId, LocalDateTime start, LocalDateTime end);
    
    Map<String, Double> getHealthMetricsAverageByDevice(Long deviceId);
    
    Double getHealthMetricAverageByDeviceAndType(Long deviceId, String type, LocalDateTime start, LocalDateTime end);
    
    void analyzeHealthMetrics(Long deviceId);
} 