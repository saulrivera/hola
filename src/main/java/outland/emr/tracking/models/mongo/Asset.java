package outland.emr.tracking.models.mongo;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Asset {
    @Id
    private String id;
    private String label;
    private AssetKind kind;

    public Asset() { }

    public Asset(String id, String label, AssetKind kind) {
        this(label, kind);
        this.id = id;
    }

    public Asset(String label, AssetKind kind) {
        this.id = UUID.randomUUID().toString();
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

