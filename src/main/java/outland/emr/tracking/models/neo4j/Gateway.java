package outland.emr.tracking.models.neo4j;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node(primaryLabel = "Gateway")
public class Gateway {
    @Id
    @GeneratedValue
    private long id;
    private String label;
    private String mac;
    private int floor;
    private double coordinateX;
    private double coordinateY;
    private double a;
    private double b;
    @Relationship(type = "siblings")
    private Set<Gateway> siblings = new HashSet<>();

    public Gateway() {}

    public Gateway(long id, String label, String mac, int floor, double coordinateX, double coordinateY, double a, double b) {
        this.id = id;
        this.label = label;
        this.mac = mac;
        this.floor = floor;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.a = a;
        this.b = b;
    }

    public Gateway(long id, String label, String mac, int floor, double coordinateX, double coordinateY, double a, double b, Set<Gateway> siblings) {
        this(id, label, mac, floor, coordinateX, coordinateY, a, b);
        this.siblings = siblings;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        this.mac = mac;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
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

    public Set<Gateway> getSiblings() {
        return siblings;
    }

    public void setSiblings(Set<Gateway> siblings) {
        this.siblings = siblings;
    }
}
