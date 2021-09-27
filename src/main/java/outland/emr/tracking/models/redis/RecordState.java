package outland.emr.tracking.models.redis;

import outland.emr.tracking.models.BeaconType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RecordState implements Serializable {
    private String trackingMac;
    private String gatewayMac;
    private double rssi;
    private BeaconType type;
    private Map<String, RecordStateGatewayParameters> gatewayParameters;

    public RecordState() {}

    public RecordState(String trackingMac, String gatewayMac, double rssi, BeaconType type) {
        this.trackingMac = trackingMac;
        this.gatewayMac = gatewayMac;
        this.rssi = rssi;
        this.type = type;
        this.gatewayParameters = new HashMap<>();
    }

    public RecordState(String trackingMac, String gatewayMac, double rssi, BeaconType type, Map<String, RecordStateGatewayParameters> gatewayParameters) {
        this(trackingMac, gatewayMac, rssi, type);
        this.gatewayParameters = gatewayParameters;
    }

    public String getTrackingMac() {
        return trackingMac;
    }

    public void setTrackingMac(String trackingMac) {
        this.trackingMac = trackingMac;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
    }

    public Map<String, RecordStateGatewayParameters> getGatewayParameters() {
        return gatewayParameters;
    }

    public void setGatewayParameters(Map<String, RecordStateGatewayParameters> gatewayParameters) {
        this.gatewayParameters = gatewayParameters;
    }
}
