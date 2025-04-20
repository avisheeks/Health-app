package com.vansh.healthapp.service;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.model.Patient;
import com.vansh.healthapp.repository.DeviceRepository;
import com.vansh.healthapp.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final PatientRepository patientRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository, PatientRepository patientRepository) {
        this.deviceRepository = deviceRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
    }

    @Override
    public Device getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with serial number: " + serialNumber));
    }

    @Override
    public List<Device> getDevicesByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        return deviceRepository.findByPatient(patient);
    }

    @Override
    @Transactional
    public Device registerDevice(Device device) {
        if (deviceRepository.existsBySerialNumber(device.getSerialNumber())) {
            throw new IllegalArgumentException("Device with serial number " + device.getSerialNumber() + " already exists");
        }
        
        device.setAssignedAt(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    @Override
    @Transactional
    public Device assignDeviceToPatient(Long deviceId, Long patientId) {
        Device device = getDeviceById(deviceId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        device.setPatient(patient);
        device.setAssignedAt(LocalDateTime.now());
        
        return deviceRepository.save(device);
    }

    @Override
    @Transactional
    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = getDeviceById(id);
        
        device.setModel(deviceDetails.getModel());
        
        // If the serial number is being changed, check if it's unique
        if (!device.getSerialNumber().equals(deviceDetails.getSerialNumber())) {
            if (deviceRepository.existsBySerialNumber(deviceDetails.getSerialNumber())) {
                throw new IllegalArgumentException("Device with serial number " + deviceDetails.getSerialNumber() + " already exists");
            }
            device.setSerialNumber(deviceDetails.getSerialNumber());
        }
        
        return deviceRepository.save(device);
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        Device device = getDeviceById(id);
        deviceRepository.delete(device);
    }
} 