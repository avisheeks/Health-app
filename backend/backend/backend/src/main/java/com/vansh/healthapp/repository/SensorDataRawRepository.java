package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.SensorDataRaw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorDataRawRepository extends JpaRepository<SensorDataRaw, Long> {
    List<SensorDataRaw> findByDevice(Device device);
    
    List<SensorDataRaw> findByDeviceAndTimestampBetween(Device device, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT s FROM SensorDataRaw s WHERE s.device.id = :deviceId ORDER BY s.timestamp DESC LIMIT 1")
    SensorDataRaw findLatestByDeviceId(Long deviceId);
} 