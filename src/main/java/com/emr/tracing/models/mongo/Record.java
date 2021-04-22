package com.emr.tracing.models.mongo;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Record {
    @Id
    private String id;
    private String trackingId;
    private String gatewayId;
    private double rssi;
    private double calibratedRssi1m;

    public Record(String trackingId, String gatewayId, double rssi, double calibratedRssi1m) {
        this.id = UUID.randomUUID().toString();
        this.trackingId = trackingId;
        this.gatewayId = gatewayId;
        this.rssi = rssi;
        this.calibratedRssi1m = calibratedRssi1m;
    }

    public String getId() {
        return id;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
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
