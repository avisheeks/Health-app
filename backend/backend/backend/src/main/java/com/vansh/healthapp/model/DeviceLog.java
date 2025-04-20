package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_logs")
public class DeviceLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "serial_number")
    private String serialNumber;
    
    private String model;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(columnDefinition = "TEXT")
    private String logData;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_session_id")
    private LoginSessionEntity loginSession;
    
    public DeviceLog() {
    }
    
    public DeviceLog(Long id, String serialNumber, String model, LocalDateTime assignedAt, String logData, Device device, LoginSessionEntity loginSession) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.model = model;
        this.assignedAt = assignedAt;
        this.logData = logData;
        this.device = device;
        this.loginSession = loginSession;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public String getLogData() {
        return logData;
    }
    
    public void setLogData(String logData) {
        this.logData = logData;
    }
    
    public Device getDevice() {
        return device;
    }
    
    public void setDevice(Device device) {
        this.device = device;
    }
    
    public LoginSessionEntity getLoginSession() {
        return loginSession;
    }
    
    public void setLoginSession(LoginSessionEntity loginSession) {
        this.loginSession = loginSession;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DeviceLog deviceLog = (DeviceLog) o;
        
        return id != null ? id.equals(deviceLog.id) : deviceLog.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 