package com.example.tracker.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.location.LocationManagerCompat;

import com.example.tracker.MainActivity;
import com.example.tracker.R;
import com.example.tracker.db.UserDBHandler;
import com.example.tracker.utils.Constants;
import com.example.tracker.utils.LocationHolder;
import com.example.tracker.utils.StringToInt;
import com.example.tracker.utils.UserDBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.telemetry.AppUserTurnstile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import io.realm.Realm;

import static android.media.session.PlaybackState.STATE_CONNECTING;
import static android.media.session.PlaybackState.STATE_NONE;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LocationGooglePlayServicesProvider provider;

    public static boolean isServiceRunning = false;
    public static final int LOCATION_PERMISSION_ID = 1001;
    public static final String BROADCAST_ACTION = "com.example.tracker.service.action.POST_LOCATION";
    public static final String TAG = "LOCATION SERVICE";
    public static int noOfBluetoothDevices = 0;
    private static final int NOTIFICATION_ID = 12345678;
    private static final String CHANNEL_ID = "channel_01";

    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNotificationManager;

    Context context;
    Handler handler;
    Runnable runnable;
    public Intent intent;

    //Database
    UserDBHelper userDBHelper;
    UserDBHandler userDBHandler = new UserDBHandler(this);

    //GPS
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    LocationSettingsRequest mLocationSettingsRequest;

    //Bluetooth
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    ScanCallback scanCallback;
    BluetoothLeScanner bluetoothLeScanner;
    Timer timer;public static int state = STATE_NONE;

    //Google Nearby API
    GoogleApiClient googleApiClient;

    public LocationService(){ }

    public LocationService(Context context){
        this.context = context;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startAdvertising();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public class LocalBinder extends Binder {
        public LocationService getServiceInstance(){
            return LocationService.this;
        }
    }

    public void onCreate(){
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userDBHandler.initializeDB();
        intent = new Intent(BROADCAST_ACTION);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        mNotificationManager = (NotificationManager) getSystemService((NOTIFICATION_SERVICE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //CharSequence name = getString(R.string.app_name);
            //String CHANNEL_NAME = "Location Services";
            //NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            //mNotificationManager.createNotificationChannel(notificationChannel);
            getNotification();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        if (googleApiClient.isConnected()){
            startAdvertising();
            startDiscovery();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        return mBinder;
    }

    public void onRebind(Intent intent){
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        super.onRebind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        if (intent != null){
            //startLocationListener();
            startTrackingCallback();
            sendBluetoothRequest();
            googleApiClient.connect();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        } else  {
            stopService();
        }
        return START_STICKY;
    }

    private void startAdvertising(){
        Nearby.Connections.startAdvertising(
                googleApiClient,
                userDBHandler.getKeyUniqueid(),
                Constants.SERVICE_ID,
                mConnectionLifecycleCallback,
                new AdvertisingOptions(Constants.STRATEGY)
        ).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(@NonNull Connections.StartAdvertisingResult startAdvertisingResult) {
                if (startAdvertisingResult.getStatus().isSuccess()){
                    Log.i("NEARBY SERVICES", "[#] LocationServices.java - Advertising Endpoint");
                } else {
                    Log.i("NEARBY SERVICES", "[#] LocationServices.java - Unable to Start Advertising");
                }
            }
        });
    }

    private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
            Log.i("NEARBY SERVICES", "[#] LocationServices.java - Connection Initiated: " + endPointId);
            //establishConnection(endPointId);
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endPointId, payloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution connectionResolution) {
            //markStudentAsPresent(endPointId);
            sendPayload(endPointId);
        }

        @Override
        public void onDisconnected(@NonNull String endPointId) {
            Log.i("NEARBY SERVICES", "[#] LocationServices.java - Disconnected: " + endPointId);
        }
    };

    private void sendPayload(String endPointId){
        Payload payload = Payload.fromBytes(userDBHandler.getKeyUniqueid().getBytes());
        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endPointId, payload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endPointId, @NonNull Payload payload) {
            byte[] receivedBytes = payload.asBytes();
            assert receivedBytes != null;
            Log.i("NEARBY SERVICES", "[#] LocationServices.java - Payload: " + receivedBytes.toString());
        }
        @Override
        public void onPayloadTransferUpdate(@NonNull String endPointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
        }
    };

    private void startDiscovery(){
        Nearby.Connections.startDiscovery(
                googleApiClient,
                Constants.SERVICE_ID,
                mEndpointDiscoveryCallback,
                new DiscoveryOptions(Constants.STRATEGY)
        ).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()){
                            Log.i("NEARBY SERVICES", "[#] LocationServices.java - Looking for Advertisers!");
                        } else {
                            Log.i("NEARBY SERVICES", "[#] LocationServices.java - Unable to Start Discovery");
                        }
                    }
                }
        );
    }

    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Nearby.getConnectionsClient(getApplicationContext()).requestConnection(userDBHandler.getKeyUniqueid(), endPointId,
                    new ConnectionLifecycleCallback() {
                        @Override
                        public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo connectionInfo) {
                            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endPointId, payloadCallback);
                        }

                        @Override
                        public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution connectionResolution) {
                        }

                        @Override
                        public void onDisconnected(@NonNull String endPointId) {
                        }
                    });
        }
        @Override
        public void onEndpointLost(@NonNull String endPointId) {
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {
        //String CHANNEL_ID = "Contra";
        String CONTENT_TEXT = "Tracker is running";
        String CHANNEL_NAME = "Location Services";
        String CHANNEL_ID = getString(R.string.app_name);

        Intent intent = new Intent(this, LocationService.class);

        Notification notification;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(CONTENT_TEXT)
                    .setAutoCancel(true);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(CONTENT_TEXT)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notification = builder.build();
        }
        return notification;
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (grantResults.length <= 0){
                //startLocationListener();
                startTrackingCallback();
            }
        } else {
            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private LocationRequest setupLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(10 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private LocationSettingsRequest buildLocationSettingsRequest(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        return mLocationSettingsRequest;
    }

    //----------------------------------------------------------------------------------------------GPS Logging
    public void startTrackingCallback(){
        Toast.makeText(this, "Requesting GPS", Toast.LENGTH_SHORT).show();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = setupLocationCallback();
        mLocationRequest = setupLocationRequest();
        buildLocationSettingsRequest();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback setupLocationCallback(){

        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                List<Location> locations = result.getLocations();

                for (Location location:locations){
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    long time = location.getTime();
                    String strLat = String.valueOf(lat);
                    String strLon = String.valueOf(lon);

                    LocationHolder locationHolder = new LocationHolder(location);
                    UserDBHelper userDBHelper = new UserDBHelper(userDBHandler.getKeyUniqueid());
                    userDBHandler.addLocation(locationHolder, userDBHelper);
                    int lastId = userDBHandler.getLastId();

                    final String uniqueId = userDBHandler.getKeyUniqueid();
                    double latitude = userDBHandler.getLocation(lastId).getLatitude();
                    double longitude = userDBHandler.getLocation(lastId).getLongitude();
                    //long dateTime = userDBHandler.getLocation(lastId).getTime();

                    Log.v("SERVICE LOCATION", "[#] LocationService.java - LOCATION: " + latitude + " && "
                            + longitude + " and " + " UNIQUE ID: " + uniqueId);

                    StringToInt stringToInt = new StringToInt();
                    Integer dateTime = stringToInt.convertToInt(time);

                    intent.putExtra("Latitude", lat);
                    intent.putExtra("Longitude", lon);
                    intent.putExtra("dateTime", dateTime);
                    intent.putExtra("Uid", uniqueId);
                    sendBroadcast(intent);

                    //ApolloService.startActionPostLocation(getApplicationContext(), latitude, longitude, dateTime, uniqueId);

                }
            }

        };
        return locationCallback;
    }

    /*public void startLocationListener() {

        if (isServiceRunning) return;
        isServiceRunning = true;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(1)
                .setInterval(1000);

        SmartLocation.with(this)
                .location()
                .config(LocationParams.BEST_EFFORT)
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onLocationUpdated(Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        long time = location.getTime();
                        String strLat = String.valueOf(lat);
                        String strLon = String.valueOf(lon);

                        LocationHolder locationHolder = new LocationHolder(location);
                        UserDBHelper userDBHelper = new UserDBHelper(userDBHandler.getKeyUniqueid());
                        userDBHandler.addLocation(locationHolder, userDBHelper);
                        int lastId = userDBHandler.getLastId();

                        final String uniqueId = userDBHandler.getKeyUniqueid();
                        double latitude = userDBHandler.getLocation(lastId).getLatitude();
                        double longitude = userDBHandler.getLocation(lastId).getLongitude();
                        //long dateTime = userDBHandler.getLocation(lastId).getTime();

                        Log.v("Service", "[#] LocationService.java - LOCATION: " + latitude + " && "
                                + longitude + " and " + " UNIQUE ID: " + uniqueId);

                        StringToInt stringToInt = new StringToInt();
                        Integer dateTime = stringToInt.convertToInt(time);

                        intent.putExtra("Latitude", lat);
                        intent.putExtra("Longitude", lon);
                        intent.putExtra("dateTime", dateTime);
                        intent.putExtra("Uid", uniqueId);
                        sendBroadcast(intent);

                        ApolloService.startActionPostLocation(getApplicationContext(), latitude, longitude, dateTime, uniqueId);

                    }
                });
    }*/

    //----------------------------------------------------------------------------------------------Bluetooth Logging
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeBluetooth(){
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.enable();

        while(!bluetoothAdapter.isEnabled()){
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            }
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        String address = bluetoothAdapter.getAddress();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendBluetoothRequest(){
        initializeBluetooth();

        final ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        builder.setReportDelay(1000);

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (bluetoothLeScanner == null){
                    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                }
                if (scanCallback == null){
                    scanCallback = initializeCallback();
                }
                bluetoothLeScanner.startScan(null, builder.build(), scanCallback);
            }
        };
        //TODO edit to 30mins at least
        long TIME_DELAY = 10000;
        long TIME_PERIOD = 10000;
        timer.schedule(timerTask, TIME_DELAY, TIME_PERIOD);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ScanCallback initializeCallback() {
        ScanCallback scanCallback = new ScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                BluetoothDevice device = result.getDevice();

                int rssi = result.getRssi();
                int txPower = result.getTxPower();
                String time = getBluetoothTime(result.getTimestampNanos());
                String address = device.getAddress();
                Parcelable[] uuids = intent.getParcelableArrayExtra(device.EXTRA_UUID);

                Log.d("SERVICE BLUETOOTH", "[#]LocationServices.java - BLUETOOTH: "
                        + uuids
                        + " && " + address
                        + " && " + rssi
                        + " && " + txPower
                        + " && " + time);

                //Toast.makeText(IntegratedLocationService.this, "Address: " + address + "\nRSSI: "+ rssi + "\nTx Power: " + txPower, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                //Log.d("Service", "Bluetooth captured data!");
                for (ScanResult result:results){
                    BluetoothDevice device = result.getDevice();

                    int rssi = result.getRssi();
                    int txPower = result.getTxPower();
                    String name = device.getName();
                    String time = getBluetoothTime(result.getTimestampNanos());
                    String address = device.getAddress();
                    Parcelable[] uuids = device.getUuids();
                    Double d = Math.pow(10,(txPower - rssi)/(10*2.25));


                    Log.d("SERVICE BLUETOOTH", "[#] LocationServices.java - BLUETOOTH: "
                            + uuids
                            + " && " + address
                            + " && " + rssi
                            + " && " + txPower
                            + " && " + time
                            + " && " + d);
                }
                noOfBluetoothDevices = results.size();
            }
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        return scanCallback;
    }

    private String getBluetoothTime(long time){
        long timeStampMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime() + time / 1000000;
        Date date = new Date(timeStampMillis);
        @SuppressLint("SimpleDateFormat") String dateTime = new SimpleDateFormat("yyyyMMdd - hh:mm:ss").format(date);
        return dateTime;
    }

    public void onStopLocationListener(){
        //SmartLocation.with(this).location().stop();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onStopBluetoothScanner(){
        if (bluetoothLeScanner != null){
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    //----------------------------------------------------------------------------------------------Nearby Connection


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        onStopLocationListener();
        onStopBluetoothScanner();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void stopService(){
        stopForeground(true);
        onStopLocationListener();
        onStopBluetoothScanner();
        stopSelf();
        isServiceRunning = false;
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

}

