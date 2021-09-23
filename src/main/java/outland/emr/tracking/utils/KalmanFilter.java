package outland.emr.tracking.utils;

public class KalmanFilter {
    private double A = 0.42093;
    private double B = 0.9476;
    private double C = 0.54992;
    private double R;
    private double Q;
    private double cov = Double.NaN;
    private double x = Double.NaN;

    public KalmanFilter(double r, double q, double a, double b, double c, double cov, double x) {
        A = a;
        B = b;
        C = c;
        R = r;
        Q = q;
        this.cov = cov;
        this.x = x;
    }

    public KalmanFilter(double r, double q) {
        R = r;
        Q = q;
    }

    public double filter(Double measurement, double u) {
        if (Double.isNaN(x)) {
            x = 1 / C * measurement;
            cov = 1 / C * Q * (1 / C);
        } else {
            double predX = A * x + B * u;
            double predCov = A * cov * A + R;

            double K = predCov * C * (1 / (C * predCov * C + Q));

            x = predX + K * (measurement - C * predX);
            cov = predCov - K * C * predCov;
        }
        return x;
    }

    public double filter(double measurement) {
        double u = 0.0;
        if (Double.isNaN(x)) {
            x = 1 / C * measurement;
            cov = 1 / C * Q * (1 / C);
        } else {
            double predX = A * x + B * u;
            double predCov = A * cov * A + R;

            // Kalman gain
            double K = predCov * C * (1 / (C * predCov * C + Q));

            // Correction
            x = predX + K * (measurement - C * predX);
            cov = predCov - K * C * predCov;
        }
        return x;
    }

    public double lastMeasurement() {
        return x;
    }

    public void setMeasurementNoise(double noise) {
        Q = noise;
    }

    public void setProcessNoise(double noise) {
        R = noise;
    }

    public double getA() {
        return A;
    }

    public double getB() {
        return B;
    }

    public double getC() {
        return C;
    }

    public double getCov() {
        return cov;
    }

    public double getX() {
        return x;
    }
}
