package com.emr.tracing.models.socket;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.redis.Patient;

public class Stream {
    private String mac;
    private double rssi;
    private double calibratedRssi1m;
    private BeaconType type;
    private String gatewayMac;
    private String gatewayLabel;
    private int gatewayFloor;
    private double gatewayCoordinateX;
    private double gatewayCoordinateY;

    public Stream(
            String mac,
            double rssi,
            double calibratedRssi1m,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY
    ) {
        this.mac = mac;
        this.rssi = rssi;
        this.calibratedRssi1m = calibratedRssi1m;
        this.type = type;
        this.gatewayMac = gatewayMac;
        this.gatewayFloor = gatewayFloor;
        this.gatewayCoordinateX = gatewayCoordinateX;
        this.gatewayCoordinateY = gatewayCoordinateY;
        this.gatewayLabel = gatewayLabel;
    }

    public Stream() {}

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getGatewayLabel() {
        return gatewayLabel;
    }

    public void setGatewayLabel(String gatewayLabel) {
        this.gatewayLabel = gatewayLabel;
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
    
    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public int getGatewayFloor() {
        return gatewayFloor;
    }

    public void setGatewayFloor(int gatewayFloor) {
        this.gatewayFloor = gatewayFloor;
    }

    public double getGatewayCoordinateX() {
        return gatewayCoordinateX;
    }

    public void setGatewayCoordinateX(double gatewayCoordinateX) {
        this.gatewayCoordinateX = gatewayCoordinateX;
    }

    public double getGatewayCoordinateY() {
        return gatewayCoordinateY;
    }

    public void setGatewayCoordinateY(double gatewayCoordinateY) {
        this.gatewayCoordinateY = gatewayCoordinateY;
    }
}
