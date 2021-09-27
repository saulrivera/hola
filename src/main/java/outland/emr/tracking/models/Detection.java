package outland.emr.tracking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Detection {
    @JsonProperty("mac")
    private String trackingMac;

    @JsonProperty("type")
    private DetectionType type;

    public String getTrackingMac() {
        return trackingMac;
    }

    public void setTrackingMac(String trackingMac) {
        this.trackingMac = trackingMac;
    }

    public DetectionType getType() {
        return type;
    }

    public void setType(DetectionType type) {
        this.type = type;
    }
}
