package com.envisage.tracker.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.envisage.tracker.service.LocationService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    public BootBroadcastReceiver() {
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent startServiceIntent = new Intent(context, LocationService.class);
                context.startForegroundService(startServiceIntent);
            } else {
                Intent startServiceIntent = new Intent(context, LocationService.class);
                context.startService(startServiceIntent);
            }
        }
    }
}
