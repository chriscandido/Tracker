package com.example.tracker.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tracker.service.LocationService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    public BootBroadcastReceiver() {
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            Intent startServiceIntent = new Intent(context, LocationService.class);
            context.startService(startServiceIntent);
        }
    }
}
