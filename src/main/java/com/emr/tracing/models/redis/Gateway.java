package com.emr.tracing.models.redis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Gateway implements Serializable {
    private String mac;
    private String label;
    private int floor;
    private double coordinateX;
    private double coordinateY;
    private Set<String> siblings;

    public Gateway() {}

    public Gateway(String mac, String label, int floor, double coordinateX, double coordinateY) {
        this.mac = mac;
        this.label = label;
        this.floor = floor;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.siblings = new HashSet<>();
    }

    public Gateway(String mac, String label, int floor, double coordinateX, double coordinateY, Set<String> siblings) {
        this(mac, label, floor, coordinateX, coordinateY);
        this.siblings = siblings;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public Set<String> getSiblings() {
        return siblings;
    }

    public void setSiblings(Set<String> siblings) {
        this.siblings = siblings;
    }
}
