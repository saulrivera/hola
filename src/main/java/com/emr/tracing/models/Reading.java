package com.emr.tracing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reading {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonProperty("TimeStamp")
    private String timestamp;
    @JsonProperty("BLEMac(hex)")
    private String trackingMac;
    @JsonProperty("DeviceMac(hex)")
    private String gatewayMac;
    @JsonProperty("RSSI(dBm)")
    private double rssi;
    @JsonProperty("RSSI@1m(dBm)")
    private double calibratedRssi1m;

    public String getId() { return id; }

    @JsonIgnore
    public void setId(String id) { this.id = id; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTrackingMac() {
        return trackingMac;
    }

    public void setTrackingMac(String trackingMac) {
        this.trackingMac = trackingMac;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public double getCalibratedRssi1m() {
        return calibratedRssi1m;
    }

    public void setCalibratedRssi1m(double calibratedRssi1m) {
        this.calibratedRssi1m = calibratedRssi1m;
    }
}
