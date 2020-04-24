package com.example.tracker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tracker.utils.Constants;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class LocationService extends Service  {

    public static boolean isServiceRunning = false;
    final LocationServiceBinder binder = new LocationServiceBinder();
    private LocationGooglePlayServicesProvider provider;
    Context context;
    Handler handler;
    Runnable runnable;

    public LocationService(){
    }

    public LocationService(Context context){
        this.context = context;
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService()  {
            return LocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent != null){
            startLocationListener();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        } else {
            stopService();
        }
        return START_STICKY;
    }

    private void startLocationListener() {

        if (isServiceRunning) return;
        isServiceRunning = true;

        long mLocTrackingInterval = 1000;
        float trackingDistance = 1;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(this)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        String strLat = String.valueOf(lat);
                        String strLon = String.valueOf(lon);
                        Log.v("Location: ",  strLat + " && " + strLon);
                        Toast.makeText(LocationService.this, "Location: " + strLat + " && " + strLon, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void stopService(){
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

}
