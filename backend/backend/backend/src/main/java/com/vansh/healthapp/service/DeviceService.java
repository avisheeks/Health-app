package com.vansh.healthapp.service;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.Patient;

import java.util.List;

public interface DeviceService {
    List<Device> getAllDevices();
    
    Device getDeviceById(Long id);
    
    Device getDeviceBySerialNumber(String serialNumber);
    
    List<Device> getDevicesByPatient(Long patientId);
    
    Device registerDevice(Device device);
    
    Device assignDeviceToPatient(Long deviceId, Long patientId);
    
    Device updateDevice(Long id, Device deviceDetails);
    
    void deleteDevice(Long id);
} 