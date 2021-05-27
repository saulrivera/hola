package com.emr.tracing.models.socket;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.redis.Staff;

public class StaffStream extends Stream {
    private Staff staff;

    public StaffStream(
            String mac,
            double rssi,
            double calibratedRssi1m,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Staff staff
    ) {
        super(mac, rssi, calibratedRssi1m, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY);
        this.staff = staff;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
