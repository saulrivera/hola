package outland.emr.tracking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tracking")
public class TrackingConfProperties {
    private int frequency;
    private double rKalmanFilter;
    private double qKalmanFilter;
    private double environmentFactor;
    private double thresholdSignal;
    private String beaconAlertId;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getrKalmanFilter() {
        return rKalmanFilter;
    }

    public void setrKalmanFilter(double rKalmanFilter) {
        this.rKalmanFilter = rKalmanFilter;
    }

    public double getqKalmanFilter() {
        return qKalmanFilter;
    }

    public void setqKalmanFilter(double qKalmanFilter) {
        this.qKalmanFilter = qKalmanFilter;
    }

    public double getEnvironmentFactor() {
        return environmentFactor;
    }

    public void setEnvironmentFactor(double environmentFactor) {
        this.environmentFactor = environmentFactor;
    }

    public double getThresholdSignal() { return thresholdSignal; }

    public void setThresholdSignal(double thresholdSignal) { this.thresholdSignal = thresholdSignal; }

    public String getBeaconAlertId() {
        return beaconAlertId;
    }

    public void setBeaconAlertId(String beaconAlertId) {
        this.beaconAlertId = beaconAlertId;
    }
}
