package outland.emr.tracking.models.csv;

import com.opencsv.bean.CsvBindByName;

public class Gateway {
    @CsvBindByName(column = "id")
    private int id;
    @CsvBindByName(column = "label")
    private String label;
    @CsvBindByName(column = "mac")
    private String mac;
    @CsvBindByName(column = "floor")
    private int floor;
    @CsvBindByName(column = "positionX")
    private double positionX;
    @CsvBindByName(column = "positionY")
    private double positionY;
    @CsvBindByName(column = "a")
    private double a;
    @CsvBindByName(column = "b")
    private double b;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac.trim().toUpperCase();
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
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
}
