package com.emr.tracing.models.redis;

import java.io.Serializable;

public class AssetBeacon implements Serializable {
    private String assetId;
    private String beaconId;

    public AssetBeacon() { }

    public AssetBeacon(String assetId, String beaconId) {
        this.assetId = assetId;
        this.beaconId = beaconId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
