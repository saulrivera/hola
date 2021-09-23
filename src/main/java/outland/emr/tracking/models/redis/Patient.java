package outland.emr.tracking.models.redis;

import java.io.Serializable;

public class Patient implements Serializable {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String room;

    public Patient() {}

    public Patient(String id, String firstName, String middleName, String lastName, String room) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
