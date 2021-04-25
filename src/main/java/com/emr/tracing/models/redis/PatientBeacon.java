package com.emr.tracing.models.redis;

import java.io.Serializable;

public class PatientBeacon implements Serializable {
    private String patientId;
    private String beaconMac;
    private boolean active;

    public PatientBeacon() {}

    public PatientBeacon(String patientId, String beaconMac, boolean active) {
        this.patientId = patientId;
        this.beaconMac = beaconMac;
        this.active = active;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getBeaconMac() {
        return beaconMac;
    }

    public void setBeaconMac(String beaconMac) {
        this.beaconMac = beaconMac;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
