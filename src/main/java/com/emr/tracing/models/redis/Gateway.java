package com.emr.tracing.models.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Gateway implements Serializable {
    private String mac;
    private String uniqueId;
    private int floor;
    private double coordinateX;
    private double coordinateY;
    private List<String> siblings;

    public Gateway(String mac, String uniqueId, int floor, double coordinateX, double coordinateY) {
        this.mac = mac;
        this.uniqueId = uniqueId;
        this.floor = floor;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.siblings = new ArrayList<>();
    }

    public Gateway(String mac, String uniqueId, int floor, double coordinateX, double coordinateY, List<String> siblings) {
        this(mac, uniqueId, floor, coordinateX, coordinateY);
        this.siblings = siblings;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
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

    public List<String> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<String> siblings) {
        this.siblings = siblings;
    }
}
