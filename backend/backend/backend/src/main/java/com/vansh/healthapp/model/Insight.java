package com.vansh.healthapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "insights")
public class Insight {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @OneToOne(mappedBy = "insight", cascade = CascadeType.ALL, orphanRemoval = true)
    private HealthReport healthReport;
    
    public Insight() {
    }
    
    public Insight(Long id, LocalDateTime generatedAt, String content) {
        this.id = id;
        this.generatedAt = generatedAt;
        this.content = content;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public HealthReport getHealthReport() {
        return healthReport;
    }
    
    public void setHealthReport(HealthReport healthReport) {
        this.healthReport = healthReport;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Insight insight = (Insight) o;
        
        return Objects.equals(id, insight.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 