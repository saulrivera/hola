package outland.emr.tracking.models.socket;

import org.joda.time.DateTime;
import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.redis.Asset;

import java.util.Date;

public class AssetStream extends Stream {
    private Asset asset;

    public AssetStream() {
        super();
    }

    public AssetStream(
            String mac,
            double rssi,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Asset asset,
            Date timestamp
    ) {
        super(mac, rssi, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY, timestamp);
        this.asset = asset;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
