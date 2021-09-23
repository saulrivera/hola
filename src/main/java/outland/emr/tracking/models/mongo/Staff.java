package outland.emr.tracking.models.mongo;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Staff {
    @Id
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private StaffKind kind;

    public Staff() { }

    public Staff(String id, String firstName, String middleName, String lastName, StaffKind kind) {
        this(firstName, middleName, lastName, kind);
        this.id = id;
    }

    public Staff(String firstName, String middleName, String lastName, StaffKind kind) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.kind = kind;
    }

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
}

