package outland.emr.tracking.models.csv;

import outland.emr.tracking.models.mongo.AssetKind;
import com.opencsv.bean.CsvBindByName;

public class Asset {
    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "label")
    private String label;
    @CsvBindByName(column = "kind")
    private AssetKind kind;
    @CsvBindByName(column = "beaconId")
    private String beaconId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public AssetKind getKind() {
        return kind;
    }

    public void setKind(AssetKind kind) {
        this.kind = kind;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
