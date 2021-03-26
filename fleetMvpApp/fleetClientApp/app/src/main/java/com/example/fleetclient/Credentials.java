package com.example.fleetclient;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credentials extends RealmObject {

    @PrimaryKey
    private int credentialId;


    private String ownerContact;
    private String ownerName;
    private String ownerAddress;
    private String ownerBusiness;
    private String driverContact;
    private String driverName;

    public int getCredentialId(){ return credentialId; }
    public String getDriverContact(){ return driverContact; }
    public String getDriverName(){ return driverName; }
    public String getOwnerContact(){ return ownerContact; }
    public String getOwnerName(){ return ownerName; }
    public String getOwnerAddress(){ return ownerAddress; }
    public String getOwnerBusiness(){ return ownerBusiness; }

    public void setCredentialId(int credentialId){ this.credentialId = credentialId; }
    public void setDriverContact(String driverContact){ this.driverContact = driverContact; }
    public void setDriverName(String driverName){ this.driverName = driverName; }
    public void setOwnerContact(String ownerContact){ this.ownerContact = ownerContact; }
    public void setOwnerName(String ownerName){ this.ownerName = ownerName; }
    public void setOwnerAddress(String ownerAddress){ this.ownerAddress = ownerAddress; }
    public void setOwnerBusiness(String ownerBusiness){ this.ownerBusiness = ownerBusiness; }

}
