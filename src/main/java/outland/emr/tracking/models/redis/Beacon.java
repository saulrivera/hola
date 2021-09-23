package outland.emr.tracking.models.redis;

import outland.emr.tracking.models.BeaconType;

import java.io.Serializable;

public class Beacon implements Serializable {
    private String mac;
    private String uniqueId;
    private BeaconType type;

    public Beacon() {}

    public Beacon(String mac, String uniqueId, BeaconType type) {
        this.mac = mac;
        this.uniqueId = uniqueId;
        this.type = type;
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

    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
    }
}
