package com.emr.tracing.models.mongo;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

public class PatientBeacon {
    @Id
    private String id;
    private String patientId;
    private String beaconId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PatientBeacon(String patientId, String beaconId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.beaconId = beaconId;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
