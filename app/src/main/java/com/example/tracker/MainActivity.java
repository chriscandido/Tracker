package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.tracker.db.UserDBHandler;
import com.example.tracker.service.LocationService;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int LOCATION_PERMISSION_ID = 1001;
    UserDBHandler userDBHandler = new UserDBHandler(this);

    private LocationService locationService;
    private boolean mBound = false;

    String uid;

    Button button_Start, button_Stop;
    ImageButton button_Dashboard;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.frameLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();

        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        animationDrawable.start();

        button_Start = findViewById(R.id.button_startService);
        button_Stop = findViewById(R.id.button_stopService);
        button_Dashboard = findViewById(R.id.image_Setting);

        button_Start.setOnClickListener(new onClick());
        button_Stop.setOnClickListener(new onClick());
        //setSupportActionBar(toolbar);

        userDBHandler.initializeDB();
        onClickDashboard();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isServiceRunning(LocationService.class)){
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        } else {
            stopService();
        }

    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getServiceInstance();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            mBound = false;
        }
    };

    public class onClick implements View.OnClickListener{
        @Override
        public void onClick(View view){
            switch (view.getId()){
                case R.id.button_startService:
                    button_Start.setVisibility(View.GONE);
                    button_Stop.setVisibility(View.VISIBLE);
                    startService();
                    break;
                case R.id.button_stopService:
                    button_Start.setVisibility(View.VISIBLE);
                    button_Stop.setVisibility(View.GONE);
                    stopService();
                    break;
            }
        }
    }

    public void onClickDashboard(){
        button_Dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
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


    public void startService(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        startService(new Intent(getBaseContext(), LocationService.class));
    }

    public void onStart(){
        super.onStart();
        if (!checkPermissions()){
            requestPermissions();
        }
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
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

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
    }
}



