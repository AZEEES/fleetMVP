package com.example.fleetclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String driver_contact = "";
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        try {
            driver_contact = credentials.get(0).getDriverContact();
//            Toast.makeText(this, driver_contact, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Credentials credential = realm.createObject(Credentials.class, 1);
            credential.setDriverName("");
            credential.setDriverContact("");
            credential.setOwnerName("");
            credential.setOwnerContact("");
            credential.setOwnerBusiness("");
            credential.setOwnerAddress("");
            realm.commitTransaction();
        }
        realm.close();
        if((driver_contact.equals(""))) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        else {
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }
    }
}