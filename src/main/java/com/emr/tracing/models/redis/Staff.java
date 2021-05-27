package com.emr.tracing.models.redis;

import com.emr.tracing.models.mongo.StaffKind;

import java.io.Serializable;

public class Staff implements Serializable {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private StaffKind kind;

    public Staff() { }

    public Staff(String id, String firstName, String middleName, String lastName, StaffKind kind) {
        this.id = id;
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
