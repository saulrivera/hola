package com.emr.tracing.models.socket;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.redis.Asset;

public class AssetStream extends Stream {
    private Asset asset;

    public AssetStream() {
        super();
    }

    public AssetStream(
            String mac,
            double rssi,
            double calibratedRssi1m,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Asset asset
    ) {
        super(mac, rssi, calibratedRssi1m, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY);
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
