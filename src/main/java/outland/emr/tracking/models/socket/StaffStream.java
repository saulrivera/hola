package outland.emr.tracking.models.socket;

import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.redis.Staff;

public class StaffStream extends Stream {
    private Staff staff;

    public StaffStream() {}

    public StaffStream(
            String mac,
            double rssi,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Staff staff
    ) {
        super(mac, rssi, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY);
        this.staff = staff;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
