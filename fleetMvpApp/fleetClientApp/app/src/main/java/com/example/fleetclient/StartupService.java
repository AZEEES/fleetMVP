package com.example.fleetclient;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import io.realm.Realm;
import io.realm.RealmResults;

public class StartupService extends Service {

    private static final int NOTIFICATION_ID = 12345678;
    private static final String CHANNEL_ID = "channel_01";

    private static final String TAG = "MyService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        Toast.makeText(this, "Startup Service creator called", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Startup Service creator called");
    }

    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

//    @Override
//    public void onStart(Intent intent, int startid)
//    {
//        startForeground(NOTIFICATION_ID, getNotification());
//        Intent intents = new Intent(getBaseContext(),HomeActivity.class);
//        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intents);
//        Toast.makeText(this, "My Service Started from onStart", Toast.LENGTH_LONG).show();
//        Log.d(TAG, "onStart");
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String driver_Contact = getDriverContact();
        if(driver_Contact.equals(""));
        else {
            startForeground(NOTIFICATION_ID, getNotification());
            Intent intents = new Intent(getBaseContext(), HomeActivity.class);
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intents);
            Toast.makeText(this, "My Service Started from onStarted", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onStart");
        }
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
//        return START_NOT_STICKY;
    }

    public String getDriverContact(){
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        final RealmResults<Credentials> credentials = realm.where(Credentials.class).findAll();
        String driver_contact = "NA";
        try {
            driver_contact = credentials.get(0).getDriverContact();
        }
        catch(Exception e){
            driver_contact = "";
        }
        realm.close();
        return driver_contact;
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText("Fleet Client Service")
                .setContentTitle("FleetClient Service")
//                .setContentTitle(DateFormat.getDateTimeInstance().format(new Date()))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.logo_16)
                .setTicker("Location updation service")
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        return builder.build();
    }
}
