package outland.emr.tracking.models.mongo;

import org.springframework.data.annotation.Id;

public class StaffBeacon {
    private String staffId;
    @Id
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
