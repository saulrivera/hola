package com.emr.tracing.models.redis;

import java.io.Serializable;

public class Beacon implements Serializable {
    private String mac;
    private String uniqueId;

    public Beacon(String mac, String uniqueId) {
        this.mac = mac;
        this.uniqueId = uniqueId;
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
}
