package outland.emr.tracking.models.mongo;

import outland.emr.tracking.models.BeaconType;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Beacon {
    @Id
    private String id;
    private String mac;
    private String label;
    private BeaconType type;

    public Beacon() { }

    public Beacon(String id, String mac, String label, BeaconType type) {
        this.id = id;
        this.mac = mac;
        this.label = label;
        this.type = type;
    }

    public Beacon(String mac, String label, BeaconType type) {
        this.id = UUID.randomUUID().toString();
        this.mac = mac;
        this.label = label;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

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

    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
    }
}
