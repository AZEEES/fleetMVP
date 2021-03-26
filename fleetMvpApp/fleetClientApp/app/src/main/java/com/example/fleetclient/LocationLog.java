package com.example.fleetclient;


import io.realm.RealmObject;

public class LocationLog extends RealmObject {

    private String latitude;
    private String longitude;
    private String timestamp;

    public String getLatitude(){ return latitude; }
    public String getLongitude(){ return longitude; }
    public String getTimestamp(){ return timestamp; }

    public void setLatitude(String latitude){ this.latitude = latitude; }
    public void setLongitude(String longitude){ this.longitude = longitude; }
    public void setTimestamp(String timestamp){ this.timestamp = timestamp; }

}
