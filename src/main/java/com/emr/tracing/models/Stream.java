package com.emr.tracing.models;

import com.emr.tracing.models.redis.Patient;

public class Stream {
    private String mac;
    private double rssi;
    private double calibratedRssi1m;
    private String gatewayMac;
    private int gatewayFloor;
    private double gatewayCoordinateX;
    private double gatewayCoordinateY;
    private Patient patient;

    public Stream(String mac, double rssi, double calibratedRssi1m, String gatewayMac, int gatewayFloor, double gatewayCoordinateX, double gatewayCoordinateY, Patient patient) {
        this.mac = mac;
        this.rssi = rssi;
        this.calibratedRssi1m = calibratedRssi1m;
        this.gatewayMac = gatewayMac;
        this.gatewayFloor = gatewayFloor;
        this.gatewayCoordinateX = gatewayCoordinateX;
        this.gatewayCoordinateY = gatewayCoordinateY;
        this.patient = patient;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
