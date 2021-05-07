package com.emr.tracing.models.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "Gateway")
public class Gateway {
    @Id
    @GeneratedValue
    private long id;
    private String label;
    private String mac;
    private int floor;
    private double coordinateX;
    private double coordinateY;
    @Relationship(type = "siblings")
    private Set<Gateway> siblings = new HashSet<>();

    public Gateway() {}

    public Gateway(String label, String mac, int floor, double coordinateX, double coordinateY) {
        this.label = label;
        this.mac = mac;
        this.floor = floor;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public Gateway(String label, String mac, int floor, double coordinateX, double coordinateY, Set<Gateway> siblings) {
        this(label, mac, floor, coordinateX, coordinateY);
        this.siblings = siblings;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Set<Gateway> getSiblings() {
        return siblings;
    }

    public void setSiblings(Set<Gateway> siblings) {
        this.siblings = siblings;
    }
}
