package com.example.fleetclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        Intent intent = new Intent(context, StartupService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
//        Toast.makeText(context, "Autostart activity started", Toast.LENGTH_LONG).show();
        Log.i("Autostart", "started");
    }
}
