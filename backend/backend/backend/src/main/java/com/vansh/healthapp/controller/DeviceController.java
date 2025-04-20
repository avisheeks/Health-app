package com.vansh.healthapp.controller;

import com.vansh.healthapp.model.Device;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.canAccessDevice(#id)")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }
    
    @GetMapping("/serial/{serialNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Device> getDeviceBySerialNumber(@PathVariable String serialNumber) {
        Device device = deviceService.getDeviceBySerialNumber(serialNumber);
        return ResponseEntity.ok(device);
    }
    
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or @userSecurity.isPatient(#patientId)")
    public ResponseEntity<List<Device>> getDevicesByPatient(@PathVariable Long patientId) {
        List<Device> devices = deviceService.getDevicesByPatient(patientId);
        return ResponseEntity.ok(devices);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> registerDevice(@Valid @RequestBody Device device) {
        Device registeredDevice = deviceService.registerDevice(device);
        return ResponseEntity.ok(registeredDevice);
    }
    
    @PostMapping("/{deviceId}/assign/{patientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Device> assignDeviceToPatient(
            @PathVariable Long deviceId,
            @PathVariable Long patientId) {
        Device assignedDevice = deviceService.assignDeviceToPatient(deviceId, patientId);
        return ResponseEntity.ok(assignedDevice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @Valid @RequestBody Device deviceDetails) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok(new MessageResponse("Device deleted successfully"));
    }
} 