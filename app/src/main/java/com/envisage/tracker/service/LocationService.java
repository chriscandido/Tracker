package com.envisage.tracker.service;

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
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.envisage.tracker.R;
import com.example.tracker.UpdateBluetoothDataHistoryMutation;
import com.envisage.tracker.db.UserDBHandler;
import com.example.tracker.type.BluetoothDataInput;
import com.example.tracker.type.BluetoothDataListInput;
import com.envisage.tracker.db.UserDBLocationHelper;
import com.envisage.tracker.utils.StringToInt;
import com.envisage.tracker.db.UserDBBluetoothHelper;
import com.envisage.tracker.db.UserDBHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.media.session.PlaybackState.STATE_NONE;

public class LocationService extends Service {

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
    AdvertiseSettings settings;
    Timer timer;
    Long currentTime;

    public static int state = STATE_NONE;

    //Google Nearby API
    GoogleApiClient googleApiClient;

    public LocationService(){ }

    public LocationService(Context context){
        this.context = context;
    }

    public class LocalBinder extends Binder {
        public LocationService getServiceInstance(){
            return LocationService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(){
        super.onCreate();

        currentTime = System.currentTimeMillis();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        userDBHandler.initializeDB();
        intent = new Intent(BROADCAST_ACTION);

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        mNotificationManager = (NotificationManager) getSystemService((NOTIFICATION_SERVICE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForeground(0, getNotification());
        }

        advertising();
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
            advertising();
            startTrackingCallback();
            sendBluetoothRequest();
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        } else  {
            stopService();
        }
        return START_STICKY;
    }

    //----------------------------------------------------------------------------------------------Notification Channel
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        isServiceRunning = true;

        //String CHANNEL_ID = "Contra";
        String CONTENT_TEXT = "ConTra+ is currently storing your location and bluetooth connection";
        String CHANNEL_NAME = "LocationServices";
        String CHANNEL_ID = getString(R.string.app_name);

        Intent intent = new Intent(this, LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(CONTENT_TEXT)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setAutoCancel(true);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(CONTENT_TEXT)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notification = builder.build();
        }
        notificationManager.notify(0, notification);
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

    //----------------------------------------------------------------------------------------------GPS Logging
    private LocationRequest setupLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private LocationSettingsRequest buildLocationSettingsRequest(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        return mLocationSettingsRequest;
    }

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
                    String dTime = String.valueOf(time);

                    UserDBLocationHelper userDBLocationHelper = new UserDBLocationHelper(location);
                    String userUniqueId = userDBHandler.getKeyUniqueid();
                    userDBHandler.addLocation(userDBLocationHelper, userUniqueId);
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

                    ApolloService.startActionPostLocation(getApplicationContext(), latitude, longitude, dateTime, uniqueId);

                }
            }

        };
        return locationCallback;
    }

    //----------------------------------------------------------------------------------------------Bluetooth Logging
    public BluetoothDataInput bluetoothDataInput (String macAddress, float distance, int createdAt){
        BluetoothDataInput bluetoothDataInput = BluetoothDataInput.builder()
                .macAddress(macAddress)
                .distance(distance)
                .createdAt(createdAt)
                .build();
        return bluetoothDataInput;
    }

    public BluetoothDataListInput bluetoothDataListInput (BluetoothDataInput bluetoothDataInput, String id){
        List<BluetoothDataInput> bluetoothDataInputs = new ArrayList<>();
        bluetoothDataInputs.add(bluetoothDataInput);
        BluetoothDataListInput bluetoothDataListInput = BluetoothDataListInput.builder()
                .list(bluetoothDataInputs)
                .ownerId(id)
                .build();
        return bluetoothDataListInput;
    }

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

        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(1000)
                .build();

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
                bluetoothLeScanner.startScan(null, settings, scanCallback);
            }
        };
        //TODO edit to 30mins at least
        long TIME_DELAY = 1000;
        long TIME_PERIOD = 2*60*1000;
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

                Log.d("SCAN BLUETOOTH", "[#]LocationServices.java - BLUETOOTH: "
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

                    long time = System.currentTimeMillis() - SystemClock.elapsedRealtime() + result.getTimestampNanos() / 1000000;
                    Long diffTime = (Math.abs(time - currentTime))/(1000*60);

                    Log.d("BATCHSCAN BLUETOOTH", "[#] LocationServices.java - ElapsedTime: " + diffTime);

                    if (diffTime > 0 && diffTime%10==0){
                        //MACAddress
                        String address = device.getAddress();

                        //Distance
                        int rssi = result.getRssi();
                        int txPower = result.getTxPower();
                        Double d = Math.pow(10,(txPower - rssi)/(10*2.25));
                        Float distance = Float.parseFloat(String.valueOf(d));

                        //Time
                        //String dateTime = getBluetoothTime(result.getTimestampNanos());
                        long dateTime = System.currentTimeMillis() - SystemClock.elapsedRealtime() + result.getTimestampNanos() / 1000000;
                        StringToInt stringToInt = new StringToInt();
                        Integer bluetoothTime = stringToInt.convertToInt(dateTime);

                        UserDBBluetoothHelper userDBBluetoothHelper = new UserDBBluetoothHelper(address, distance, bluetoothTime);
                        String userUniqueId = userDBHandler.getKeyUniqueid();
                        userDBHandler.addScannedBluetooth(userDBBluetoothHelper, userUniqueId);

                        final String uniqueId = userDBHandler.getKeyUniqueid();

                        BluetoothDataInput bluetoothInput = bluetoothDataInput(address, distance, bluetoothTime);
                        BluetoothDataListInput bluetoothInputList = bluetoothDataListInput(bluetoothInput, uniqueId);

                        ApolloClient.setupApollo().mutate(UpdateBluetoothDataHistoryMutation.builder().input(bluetoothInputList).build())
                                .enqueue(new ApolloCall.Callback<UpdateBluetoothDataHistoryMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<UpdateBluetoothDataHistoryMutation.Data> response) {
                                        UpdateBluetoothDataHistoryMutation.Data data = response.data();
                                        Log.v("SERVER", "[#] ApolloService.java: " + data.toString());
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {

                                    }
                                });

                        //intent.putExtra("Macaddress", address);
                        //intent.putExtra("Distance", distance);
                        //intent.putExtra("BluetoothTime", bluetoothTime);
                        //intent.putExtra("Uid", uniqueId);
                        //sendBroadcast(intent);

                        //ApolloServiceBluetooth.startActionPostBluetooth(getApplicationContext(), address, distance, bluetoothTime, uniqueId);

                        Log.d("BATCHSCAN BLUETOOTH", "[#] LocationServices.java - BLUETOOTH: "
                                + userDBHandler.getLastScannedBluetooth().getMcaddress()
                                + " && " + userDBHandler.getLastScannedBluetooth().getDistance()
                                + " && " + userDBHandler.getLastScannedBluetooth().getCreatedAd());
                    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void advertising (){
        Log.v("BLUETOOTH ADVERTISE", "[#] LocationServices.java - BLUETOOTH ADVERTISEMENT STARTED!");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final AdvertisingSet[] currentAdvertisingSet = new AdvertisingSet[1];
        final BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();

        // Check if all features are supported
        if (!bluetoothAdapter.isLe2MPhySupported()) {
            Log.e("BLUETOOTH ADVERTISE", "2M PHY not supported!");
            return;
        }
        if (!bluetoothAdapter.isLeExtendedAdvertisingSupported()) {
            Log.e("BLUETOOTH ADVERTISE", "LE Extended Advertising not supported!");
            return;
        }

        settings = (new AdvertiseSettings.Builder())
                .setConnectable(false)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .build();

        ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(userDBHandler.getKeyUniqueid()));
        AdvertiseData data = (new AdvertiseData.Builder())
                .setIncludeDeviceName(false)
                .addServiceUuid(parcelUuid)
                .build();

        Log.i("BLUETOOTH ADVERTISE", String.valueOf(data));

        AdvertiseCallback callback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.v("BLUETOOTH ADVERTISING", settingsInEffect.toString());
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        };
        advertiser.startAdvertising(settings, data, callback);
        //currentAdvertisingSet[0].enableAdvertising(true, 0, 0);
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

