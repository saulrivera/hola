package outland.emr.tracking.models.redis;


import outland.emr.tracking.models.mongo.AssetKind;

import java.io.Serializable;

public class Asset implements Serializable {
    private String id;
    private String label;
    private AssetKind kind;

    public Asset() { }

    public Asset(String id, String label, AssetKind kind) {
        this.id = id;
        this.label = label;
        this.kind = kind;
    }

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
}