package com.vansh.healthapp.controller;

import com.vansh.healthapp.model.HealthMetric;
import com.vansh.healthapp.model.SensorDataRaw;
import com.vansh.healthapp.payload.request.SensorDataRequest;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.security.UserSecurity;
import com.vansh.healthapp.service.DeviceService;
import com.vansh.healthapp.service.SensorDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;
    private final DeviceService deviceService;
    private final UserSecurity userSecurity;

    public SensorDataController(
            SensorDataService sensorDataService,
            DeviceService deviceService,
            UserSecurity userSecurity) {
        this.sensorDataService = sensorDataService;
        this.deviceService = deviceService;
        this.userSecurity = userSecurity;
    }

    @PostMapping
    public ResponseEntity<SensorDataRaw> saveSensorData(@Valid @RequestBody SensorDataRequest sensorDataRequest) {
        SensorDataRaw sensorData = sensorDataService.saveSensorData(sensorDataRequest);
        return ResponseEntity.ok(sensorData);
    }

    @GetMapping("/device/{deviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<List<SensorDataRaw>> getSensorDataByDevice(@PathVariable Long deviceId) {
        List<SensorDataRaw> sensorData = sensorDataService.getSensorDataByDevice(deviceId);
        return ResponseEntity.ok(sensorData);
    }

    @GetMapping("/device/{deviceId}/latest")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<SensorDataRaw> getLatestSensorDataByDevice(@PathVariable Long deviceId) {
        SensorDataRaw sensorData = sensorDataService.getLatestSensorDataByDevice(deviceId);
        return ResponseEntity.ok(sensorData);
    }

    @GetMapping("/device/{deviceId}/range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<List<SensorDataRaw>> getSensorDataByDeviceAndTimeRange(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<SensorDataRaw> sensorData = sensorDataService.getSensorDataByDeviceAndTimeRange(deviceId, start, end);
        return ResponseEntity.ok(sensorData);
    }

    @GetMapping("/metrics/device/{deviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<List<HealthMetric>> getHealthMetricsByDevice(@PathVariable Long deviceId) {
        List<HealthMetric> healthMetrics = sensorDataService.getHealthMetricsByDevice(deviceId);
        return ResponseEntity.ok(healthMetrics);
    }

    @GetMapping("/metrics/device/{deviceId}/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<List<HealthMetric>> getHealthMetricsByDeviceAndType(
            @PathVariable Long deviceId,
            @PathVariable String type) {
        List<HealthMetric> healthMetrics = sensorDataService.getHealthMetricsByDeviceAndType(deviceId, type);
        return ResponseEntity.ok(healthMetrics);
    }

    @GetMapping("/metrics/device/{deviceId}/averages")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#deviceId)")
    public ResponseEntity<Map<String, Double>> getHealthMetricsAverageByDevice(@PathVariable Long deviceId) {
        Map<String, Double> averages = sensorDataService.getHealthMetricsAverageByDevice(deviceId);
        return ResponseEntity.ok(averages);
    }

    @PostMapping("/analyze/device/{deviceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<MessageResponse> analyzeHealthMetrics(@PathVariable Long deviceId) {
        sensorDataService.analyzeHealthMetrics(deviceId);
        return ResponseEntity.ok(new MessageResponse("Health metrics analyzed successfully"));
    }
} 