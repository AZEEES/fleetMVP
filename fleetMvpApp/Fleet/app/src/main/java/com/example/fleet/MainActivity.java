package com.example.fleet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FleetMainActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String owner_contact = "";
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        try {
            owner_contact = credentials.get(0).getOwnerContact();
        }
        catch (Exception e){
            Credentials credential = realm.createObject(Credentials.class, 1);
            credential.setOwnerName("");
            credential.setOwnerContact("");
            credential.setOwnerBusiness("");
            credential.setOwnerAddress("");
            realm.commitTransaction();
        }
        realm.close();

        Log.v(TAG,"Owner_Contact : (" +  owner_contact + ")");
        if((owner_contact.equals(""))) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        else {
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }

    }
}