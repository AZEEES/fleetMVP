package com.example.fleetclient;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FleetClientApplication extends Application {

    private String mServerIp = "10.0.2.2:3000";


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();

    }

    public String get_Server_IP(){
        return mServerIp;
    }
}
