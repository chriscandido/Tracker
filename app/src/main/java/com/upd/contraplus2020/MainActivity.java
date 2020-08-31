package com.upd.contraplus2020;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.upd.contraplus2020.db.UserDBHandler;
import com.upd.contraplus2020.service.LocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.skyfishjy.library.RippleBackground;

import static io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS;

public class MainActivity extends AppCompatActivity {

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int LOCATION_PERMISSION_ID = 1001;

    UserDBHandler userDBHandler = new UserDBHandler(this);

    private LocationService locationService;
    private boolean mBound = false;

    Button button_Start, button_Stop;
    ImageButton button_Dashboard;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_Start = findViewById(R.id.button_startService);
        button_Stop = findViewById(R.id.button_stopService);
        rippleBackground = (RippleBackground) findViewById(R.id.content);

        button_Start.setOnClickListener(new onClick());
        button_Stop.setOnClickListener(new onClick());

        userDBHandler.initializeDB();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isServiceRunning(LocationService.class)){
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
            displayLocationSettingsRequest(this);
        } else {
            stopService();
        }

    }

    public class onClick implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.button_startService:
                    button_Start.setVisibility(View.GONE);
                    button_Stop.setVisibility(View.VISIBLE);
                    startService();
                    rippleBackground.startRippleAnimation();
                    break;
                case R.id.button_stopService:
                    button_Start.setVisibility(View.VISIBLE);
                    button_Stop.setVisibility(View.GONE);
                    stopService();
                    rippleBackground.stopRippleAnimation();
                    break;
            }
        }
    }

    public void goToDashboard(View view){
        Intent intent = new Intent (getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
    }

    public void goToProfile(View view){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------Show location settings
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("SETTINGS", "[#] MainActivity.java - LOCATION SETTINGS: All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("SETTINGS", "[#] MainActivity.java - LOCATION SETTINGS: Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("SETTINGS", "[#] MainActivity.java - LOCATION SETTINGS: PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("SETTINGS", "[#] MainActivity.java - LOCATION SETTINGS: Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startService(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        startService(new Intent(getBaseContext(), LocationService.class));
        displayLocationSettingsRequest(this);
    }

    public void onStart(){
        super.onStart();
        if (!checkPermissions()){
            requestPermissions();
        }
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        //bindService(new Intent(this, LocationService.class), serviceConnection,
        //        Context.BIND_AUTO_CREATE);
        rippleBackground.startRippleAnimation();
    }

    public void stopService(){
        stopService(new Intent(getBaseContext(), LocationService.class));
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onBackPressed(){ }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }
}



