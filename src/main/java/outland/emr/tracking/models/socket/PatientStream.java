package outland.emr.tracking.models.socket;

import org.joda.time.DateTime;
import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.redis.Patient;

import java.util.Date;

public class PatientStream extends Stream {
    Patient patient;

    public PatientStream() {}

    public PatientStream(
            String mac,
            double rssi,
            BeaconType type,
            String gatewayMac,
            String gatewayLabel,
            int gatewayFloor,
            double gatewayCoordinateX,
            double gatewayCoordinateY,
            Patient patient,
            Date timestamp
    ) {
        super(mac, rssi, type, gatewayMac, gatewayLabel, gatewayFloor, gatewayCoordinateX, gatewayCoordinateY, timestamp);
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
