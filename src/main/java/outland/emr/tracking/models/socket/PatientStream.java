package outland.emr.tracking.models.socket;

import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.redis.Patient;

public class PatientStream extends Stream {
    Patient patient;

    public PatientStream(
            String mac,
            double rssi,
            double calibratedRssi1m,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Patient patient
    ) {
        super(mac, rssi, calibratedRssi1m, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY);
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
