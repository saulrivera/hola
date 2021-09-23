package outland.emr.tracking.models.csv;

import outland.emr.tracking.models.BeaconType;
import com.opencsv.bean.CsvBindByName;

public class Beacon {
    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "mac")
    private String mac;
    @CsvBindByName(column = "label")
    private String label;
    @CsvBindByName(column = "type")
    private BeaconType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac.trim().toUpperCase();
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

    @Override
    public String toString() {
        return "Beacon{" +
                "mac='" + mac + '\'' +
                ", label='" + label + '\'' +
                ", type=" + type +
                '}';
    }
}
