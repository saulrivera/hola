package com.emr.tracing.models.mongo;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Beacon {
    @Id
    private String id;
    private String mac;
    private String label;

    public Beacon(String mac, String label) {
        this.id = UUID.randomUUID().toString();
        this.mac = mac;
        this.label = label;
    }

    public String getId() {
        return id;
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
}
