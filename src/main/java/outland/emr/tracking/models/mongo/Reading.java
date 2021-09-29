package outland.emr.tracking.models.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import outland.emr.tracking.models.Detection;

import java.util.Date;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reading extends Detection {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonProperty("timestamp")
    private DateTime timestamp;
    @JsonProperty("rssi")
    private double rssi;
    @JsonProperty("ibeaconUuid")
    private String uuid;
    @JsonIgnore
    private String gatewayMac;

    public String getId() { return id; }

    @JsonIgnore
    public void setId(String id) { this.id = id; }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getRssi() {
        return rssi;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGatewayMac() {
        return gatewayMac;
    }

    public void setGatewayMac(String gatewayMac) {
        this.gatewayMac = gatewayMac;
    }
}
