package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByDevice(Device device);
    
    List<HealthMetric> findByDeviceAndType(Device device, String type);
    
    List<HealthMetric> findByDeviceAndTimestampBetween(Device device, LocalDateTime start, LocalDateTime end);
    
    List<HealthMetric> findByDeviceAndTypeAndTimestampBetween(Device device, String type, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT AVG(h.value) FROM HealthMetric h WHERE h.device.id = :deviceId AND h.type = :type AND h.timestamp BETWEEN :start AND :end")
    Double findAverageByDeviceAndTypeAndTimestampBetween(Long deviceId, String type, LocalDateTime start, LocalDateTime end);
} 