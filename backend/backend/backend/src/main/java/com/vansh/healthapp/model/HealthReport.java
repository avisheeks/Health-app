package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_reports")
public class HealthReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date")
    private LocalDateTime reportDate;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insight_id")
    private Insight insight;
    
    public HealthReport() {
    }
    
    public HealthReport(Long id, LocalDateTime reportDate, String summary, String recommendations, Prescription prescription, Insight insight) {
        this.id = id;
        this.reportDate = reportDate;
        this.summary = summary;
        this.recommendations = recommendations;
        this.prescription = prescription;
        this.insight = insight;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public Prescription getPrescription() {
        return prescription;
    }
    
    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
    
    public Insight getInsight() {
        return insight;
    }
    
    public void setInsight(Insight insight) {
        this.insight = insight;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        HealthReport that = (HealthReport) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 