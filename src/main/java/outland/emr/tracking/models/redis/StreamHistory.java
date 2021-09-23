package outland.emr.tracking.models.redis;

import outland.emr.tracking.models.socket.Stream;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StreamHistory implements Serializable {
    private String beaconType;
    private Map<String, Stream> streams = new HashMap<>();

    public String getBeaconType() {
        return beaconType;
    }

    public void setBeaconType(String beaconType) {
        this.beaconType = beaconType;
    }

    public Map<String, Stream> getStreams() {
        return streams;
    }

    public void setStreams(Map<String, Stream> streams) {
        this.streams = streams;
    }
}
