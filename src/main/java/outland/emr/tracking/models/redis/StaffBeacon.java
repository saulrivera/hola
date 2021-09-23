package outland.emr.tracking.models.redis;

import java.io.Serializable;

public class StaffBeacon implements Serializable {
    private String staffId;
    private String beaconId;

    public StaffBeacon() { }

    public StaffBeacon(String staffId, String beaconId) {
        this.staffId = staffId;
        this.beaconId = beaconId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
