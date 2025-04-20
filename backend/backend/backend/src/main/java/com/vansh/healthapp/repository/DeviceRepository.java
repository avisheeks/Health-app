package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByPatient(Patient patient);
    
    Optional<Device> findBySerialNumber(String serialNumber);
    
    boolean existsBySerialNumber(String serialNumber);
} 