package com.example.fleet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credentials extends RealmObject {

    @PrimaryKey
    private int credentialId;

    private String ownerContact;
    private String ownerName;
    private String ownerAddress;
    private String ownerBusiness;
    private String ownerMaxLimit;

    public int getCredentialId(){ return credentialId; }
    public String getOwnerContact(){ return ownerContact; }
    public String getOwnerName(){ return ownerName; }
    public String getOwnerAddress(){ return ownerAddress; }
    public String getOwnerBusiness(){ return ownerBusiness; }
    public String getOwnerMaxLimit(){ return ownerMaxLimit; }

    public void setCredentialId(int credentialId){ this.credentialId = credentialId; }
    public void setOwnerContact(String ownerContact){ this.ownerContact = ownerContact; }
    public void setOwnerName(String ownerName){ this.ownerName = ownerName; }
    public void setOwnerAddress(String ownerAddress){ this.ownerAddress = ownerAddress; }
    public void setOwnerBusiness(String ownerBusiness){ this.ownerBusiness = ownerBusiness; }
    public void setOwnerMaxLimit(String ownerMaxLimit){ this.ownerMaxLimit = ownerMaxLimit; }

}

