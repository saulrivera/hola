package outland.emr.tracking.models.redis;

import java.io.Serializable;

public class RecordStateGatewayParameters implements Serializable {
    private double a;
    private double b;
    private double c;
    private double cov;
    private double x;

    public RecordStateGatewayParameters() {}

    public RecordStateGatewayParameters(double a, double b, double c, double cov, double x) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.cov = cov;
        this.x = x;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getCov() {
        return cov;
    }

    public void setCov(double cov) {
        this.cov = cov;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
