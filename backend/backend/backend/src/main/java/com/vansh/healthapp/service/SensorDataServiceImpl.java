package com.vansh.healthapp.service;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.HealthAlert;
import com.vansh.healthapp.model.HealthMetric;
import com.vansh.healthapp.model.SensorDataRaw;
import com.vansh.healthapp.payload.request.SensorDataRequest;
import com.vansh.healthapp.repository.DeviceRepository;
import com.vansh.healthapp.repository.HealthAlertRepository;
import com.vansh.healthapp.repository.HealthMetricRepository;
import com.vansh.healthapp.repository.SensorDataRawRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    private final SensorDataRawRepository sensorDataRawRepository;
    private final HealthMetricRepository healthMetricRepository;
    private final DeviceRepository deviceRepository;
    private final HealthAlertRepository healthAlertRepository;

    public SensorDataServiceImpl(
            SensorDataRawRepository sensorDataRawRepository,
            HealthMetricRepository healthMetricRepository,
            DeviceRepository deviceRepository,
            HealthAlertRepository healthAlertRepository) {
        this.sensorDataRawRepository = sensorDataRawRepository;
        this.healthMetricRepository = healthMetricRepository;
        this.deviceRepository = deviceRepository;
        this.healthAlertRepository = healthAlertRepository;
    }

    @Override
    @Transactional
    public SensorDataRaw saveSensorData(SensorDataRequest sensorDataRequest) {
        Device device = deviceRepository.findBySerialNumber(sensorDataRequest.getDeviceSerialNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with serial number: " + sensorDataRequest.getDeviceSerialNumber()));

        SensorDataRaw sensorData = new SensorDataRaw();
        sensorData.setHeartRate(sensorDataRequest.getHeartRate());
        sensorData.setBloodPressureSystolic(sensorDataRequest.getBloodPressureSystolic());
        sensorData.setBloodPressureDiastolic(sensorDataRequest.getBloodPressureDiastolic());
        sensorData.setOxygenLevel(sensorDataRequest.getOxygenLevel());
        sensorData.setTemperature(sensorDataRequest.getTemperature());
        sensorData.setStepCount(sensorDataRequest.getStepCount());
        sensorData.setTimestamp(LocalDateTime.now());
        sensorData.setDevice(device);

        SensorDataRaw savedSensorData = sensorDataRawRepository.save(sensorData);
        
        // Create health metrics from the sensor data
        createHealthMetrics(savedSensorData);
        
        // Analyze the new data to generate alerts if needed
        analyzeHealthMetrics(device.getId());
        
        return savedSensorData;
    }

    private void createHealthMetrics(SensorDataRaw sensorData) {
        if (sensorData.getHeartRate() != null) {
            createHealthMetric(sensorData, "HEART_RATE", sensorData.getHeartRate());
        }
        
        if (sensorData.getBloodPressureSystolic() != null) {
            createHealthMetric(sensorData, "BLOOD_PRESSURE_SYSTOLIC", sensorData.getBloodPressureSystolic());
        }
        
        if (sensorData.getBloodPressureDiastolic() != null) {
            createHealthMetric(sensorData, "BLOOD_PRESSURE_DIASTOLIC", sensorData.getBloodPressureDiastolic());
        }
        
        if (sensorData.getOxygenLevel() != null) {
            createHealthMetric(sensorData, "OXYGEN_LEVEL", sensorData.getOxygenLevel());
        }
        
        if (sensorData.getTemperature() != null) {
            createHealthMetric(sensorData, "TEMPERATURE", sensorData.getTemperature());
        }
        
        if (sensorData.getStepCount() != null) {
            createHealthMetric(sensorData, "STEP_COUNT", sensorData.getStepCount().doubleValue());
        }
    }
    
    private void createHealthMetric(SensorDataRaw sensorData, String type, Double value) {
        HealthMetric healthMetric = new HealthMetric();
        healthMetric.setType(type);
        healthMetric.setValue(value);
        healthMetric.setTimestamp(sensorData.getTimestamp());
        healthMetric.setSensorDataRaw(sensorData);
        healthMetric.setDevice(sensorData.getDevice());
        
        healthMetricRepository.save(healthMetric);
    }

    @Override
    public List<SensorDataRaw> getSensorDataByDevice(Long deviceId) {
        Device device = getDeviceById(deviceId);
        return sensorDataRawRepository.findByDevice(device);
    }

    @Override
    public List<SensorDataRaw> getSensorDataByDeviceAndTimeRange(Long deviceId, LocalDateTime start, LocalDateTime end) {
        Device device = getDeviceById(deviceId);
        return sensorDataRawRepository.findByDeviceAndTimestampBetween(device, start, end);
    }

    @Override
    public SensorDataRaw getLatestSensorDataByDevice(Long deviceId) {
        getDeviceById(deviceId); // Verify device exists
        return sensorDataRawRepository.findLatestByDeviceId(deviceId);
    }

    @Override
    public List<HealthMetric> getHealthMetricsByDevice(Long deviceId) {
        Device device = getDeviceById(deviceId);
        return healthMetricRepository.findByDevice(device);
    }

    @Override
    public List<HealthMetric> getHealthMetricsByDeviceAndType(Long deviceId, String type) {
        Device device = getDeviceById(deviceId);
        return healthMetricRepository.findByDeviceAndType(device, type);
    }

    @Override
    public List<HealthMetric> getHealthMetricsByDeviceAndTimeRange(Long deviceId, LocalDateTime start, LocalDateTime end) {
        Device device = getDeviceById(deviceId);
        return healthMetricRepository.findByDeviceAndTimestampBetween(device, start, end);
    }

    @Override
    public Map<String, Double> getHealthMetricsAverageByDevice(Long deviceId) {
        Device device = getDeviceById(deviceId);
        
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7); // Last 7 days
        
        Map<String, Double> averages = new HashMap<>();
        
        averages.put("HEART_RATE", getHealthMetricAverageByDeviceAndType(deviceId, "HEART_RATE", start, end));
        averages.put("BLOOD_PRESSURE_SYSTOLIC", getHealthMetricAverageByDeviceAndType(deviceId, "BLOOD_PRESSURE_SYSTOLIC", start, end));
        averages.put("BLOOD_PRESSURE_DIASTOLIC", getHealthMetricAverageByDeviceAndType(deviceId, "BLOOD_PRESSURE_DIASTOLIC", start, end));
        averages.put("OXYGEN_LEVEL", getHealthMetricAverageByDeviceAndType(deviceId, "OXYGEN_LEVEL", start, end));
        averages.put("TEMPERATURE", getHealthMetricAverageByDeviceAndType(deviceId, "TEMPERATURE", start, end));
        
        return averages;
    }

    @Override
    public Double getHealthMetricAverageByDeviceAndType(Long deviceId, String type, LocalDateTime start, LocalDateTime end) {
        getDeviceById(deviceId); // Verify device exists
        return healthMetricRepository.findAverageByDeviceAndTypeAndTimestampBetween(deviceId, type, start, end);
    }

    @Override
    @Transactional
    public void analyzeHealthMetrics(Long deviceId) {
        Device device = getDeviceById(deviceId);
        SensorDataRaw latestData = sensorDataRawRepository.findLatestByDeviceId(deviceId);
        
        if (latestData == null) {
            return; // No data to analyze
        }
        
        // Check for abnormal heart rate
        if (latestData.getHeartRate() != null) {
            if (latestData.getHeartRate() > 100 || latestData.getHeartRate() < 60) {
                createHealthAlert(device, "ABNORMAL_HEART_RATE", 
                        "Abnormal heart rate detected: " + latestData.getHeartRate() + " BPM");
            }
        }
        
        // Check for abnormal blood pressure
        if (latestData.getBloodPressureSystolic() != null && latestData.getBloodPressureDiastolic() != null) {
            if (latestData.getBloodPressureSystolic() > 140 || latestData.getBloodPressureDiastolic() > 90) {
                createHealthAlert(device, "HIGH_BLOOD_PRESSURE", 
                        "High blood pressure detected: " + latestData.getBloodPressureSystolic() + "/" + 
                        latestData.getBloodPressureDiastolic() + " mmHg");
            } else if (latestData.getBloodPressureSystolic() < 90 || latestData.getBloodPressureDiastolic() < 60) {
                createHealthAlert(device, "LOW_BLOOD_PRESSURE", 
                        "Low blood pressure detected: " + latestData.getBloodPressureSystolic() + "/" + 
                        latestData.getBloodPressureDiastolic() + " mmHg");
            }
        }
        
        // Check for low oxygen levels
        if (latestData.getOxygenLevel() != null && latestData.getOxygenLevel() < 90) {
            createHealthAlert(device, "LOW_OXYGEN_LEVEL", 
                    "Low oxygen level detected: " + latestData.getOxygenLevel() + "%");
        }
        
        // Check for abnormal temperature
        if (latestData.getTemperature() != null) {
            if (latestData.getTemperature() > 38.0) {
                createHealthAlert(device, "HIGH_TEMPERATURE", 
                        "High temperature detected: " + latestData.getTemperature() + "°C");
            } else if (latestData.getTemperature() < 36.0) {
                createHealthAlert(device, "LOW_TEMPERATURE", 
                        "Low temperature detected: " + latestData.getTemperature() + "°C");
            }
        }
    }
    
    private void createHealthAlert(Device device, String alertType, String messageData) {
        HealthAlert healthAlert = new HealthAlert();
        healthAlert.setAlertType(alertType);
        healthAlert.setMessageData(messageData);
        healthAlert.setPatient(device.getPatient());
        
        healthAlertRepository.save(healthAlert);
    }
    
    private Device getDeviceById(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));
    }
} 