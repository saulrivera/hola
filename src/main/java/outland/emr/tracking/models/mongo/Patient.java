package outland.emr.tracking.models.mongo;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class Patient {
    @Id
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String room;
    private String contactPhone;
    private String contactEmail;

    public Patient() {}

    public Patient(String firstName, String lastName) {
        this(firstName, "", lastName, "", "", "");
    }

    public Patient(String id, String firstName, String middleName, String lastName, String room, String contactPhone, String contactEmail) {
        this(firstName, middleName, lastName, room, contactPhone, contactEmail);
        this.id = id;
    }

    public Patient(String firstName, String middleName, String lastName, String room, String contactPhone, String contactEmail) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.room = room;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    public String getId() { return id; }

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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String fullName() {
        if (this.middleName != null && !this.middleName.isEmpty()) {
            return this.firstName + " " + this.middleName + " " + this.lastName;
        }
        return this.firstName + " " + this.lastName;
    }
}
