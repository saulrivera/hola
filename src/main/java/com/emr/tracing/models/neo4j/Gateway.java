package com.emr.tracing.models.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node(primaryLabel = "Gateway")
public class Gateway {
    @Id
    @GeneratedValue
    private long id;
    private String uniqueId;
    private String mac;
    private int floor;
    private double coordinateX;
    private double coordinateY;
    @Relationship(type = "siblings")
    private List<Gateway> siblings;

    public Gateway(String uniqueId, String mac, int floor, double coordinateX, double coordinateY, List<Gateway> siblings) {
        this.uniqueId = uniqueId;
        this.mac = mac;
        this.floor = floor;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.siblings = siblings;
    }

    public long getId() {
        return id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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

    public List<Gateway> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<Gateway> siblings) {
        this.siblings = siblings;
    }
}
