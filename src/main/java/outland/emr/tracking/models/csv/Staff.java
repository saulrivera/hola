package outland.emr.tracking.models.csv;

import outland.emr.tracking.models.mongo.StaffKind;
import com.opencsv.bean.CsvBindByName;

public class Staff {
    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "firstName")
    private String firstName;
    @CsvBindByName(column = "middleName")
    private String middleName;
    @CsvBindByName(column = "lastName")
    private String lastName;
    @CsvBindByName(column = "kind")
    private StaffKind kind;
    @CsvBindByName(column = "beaconId")
    private String beaconId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public StaffKind getKind() {
        return kind;
    }

    public void setKind(StaffKind kind) {
        this.kind = kind;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }
}
