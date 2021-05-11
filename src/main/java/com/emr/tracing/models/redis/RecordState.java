package com.emr.tracing.models.redis;

import com.emr.tracing.models.BeaconType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RecordState implements Serializable {
    private String trackingMac;
    private String gatewayMac;
    private double rssi;
    private BeaconType type;
    private double calibratedRssi1m;
    private Map<String, RecordStateGatewayParameters> gatewayParameters;

    public RecordState() {}

    public RecordState(String trackingMac, String gatewayMac, double rssi, BeaconType type, double calibratedRssi1m) {
        this.trackingMac = trackingMac;
        this.gatewayMac = gatewayMac;
        this.rssi = rssi;
        this.type = type;
        this.calibratedRssi1m = calibratedRssi1m;
        this.gatewayParameters = new HashMap<>();
    }

    public RecordState(String trackingMac, String gatewayMac, double rssi, BeaconType type, double calibratedRssi1m, Map<String, RecordStateGatewayParameters> gatewayParameters) {
        this(trackingMac, gatewayMac, rssi, type, calibratedRssi1m);
        this.gatewayParameters = gatewayParameters;
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

    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
    }

    public double getCalibratedRssi1m() {
        return calibratedRssi1m;
    }

    public void setCalibratedRssi1m(double calibratedRssi1m) {
        this.calibratedRssi1m = calibratedRssi1m;
    }

    public Map<String, RecordStateGatewayParameters> getGatewayParameters() {
        return gatewayParameters;
    }

    public void setGatewayParameters(Map<String, RecordStateGatewayParameters> gatewayParameters) {
        this.gatewayParameters = gatewayParameters;
    }
}
