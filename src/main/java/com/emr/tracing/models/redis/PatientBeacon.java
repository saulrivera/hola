package com.emr.tracing.models.redis;

import java.io.Serializable;

public class PatientBeacon implements Serializable {
    private String patientId;
    private String beaconId;
    private boolean active;

    public PatientBeacon(String patientId, String beaconId, boolean active) {
        this.patientId = patientId;
        this.beaconId = beaconId;
        this.active = active;
    }

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
}
