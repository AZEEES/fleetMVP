package com.example.fleet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DriverData extends RealmObject {

    @PrimaryKey
    private String _id;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = _id;
    }

    public String getOwner_contact() {
        return owner_contact;
    }

    public void setOwner_contact(String owner_contact) {
        this.owner_contact = owner_contact;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess() { return access; }

    public void setAccess(String access){ this.access = access; }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    private String owner_contact;
    private String contact;
    private String name;
    private String access;
    private String latitude;
    private String longitude;
    private String locationText;
    private String timeStamp;

}
