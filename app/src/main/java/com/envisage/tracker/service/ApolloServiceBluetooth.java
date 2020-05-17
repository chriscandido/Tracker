package com.envisage.tracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tracker.type.BluetoothDataInput;
import com.example.tracker.type.BluetoothDataListInput;

import java.util.ArrayList;
import java.util.List;

public class ApolloServiceBluetooth extends IntentService {

    private final static String ACTION_POST_LOCATION = "com.example.tracker.service.action.POST_LOCATION";
    //Bluetooth
    private final static String MAC = "com.example.tracker.service.extra.Macaddress";
    private final static String DIST = "com.example.tracker.service.extra.Distance";
    private final static String BTIME = "com.example.tracker.service.extra.BluetoothTime";
    private final static String UID = "com.example.tracker.service.extra.Uid";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ApolloServiceBluetooth(String name) {
        super("ApolloServiceBluetooth");
    }

    public static void startActionPostBluetooth (Context context,
                                                 String mac,
                                                 Float dist,
                                                 Integer btime,
                                                 String uid){
        Intent intent = new Intent(context, ApolloService.class);
        intent.setAction(ACTION_POST_LOCATION);
        intent.putExtra(MAC, mac);
        intent.putExtra(DIST, dist);
        intent.putExtra(BTIME, btime);
        intent.putExtra(UID, uid);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POST_LOCATION.equals(action)) {
                //Bluetooth
                final String mac = intent.getStringExtra(MAC);
                final Float dist = intent.getFloatExtra(DIST, 0);
                final Integer btime = intent.getIntExtra(BTIME, 0);
                final String uid = intent.getStringExtra(UID);
                handleActionPostBluetooth(mac, dist, btime, uid);

            }
        } else {
            Log.v("LOCATION", "[#] ApolloService.java - Location: Null");

        }
    }

    //----------------------------------------------------------------------------------------------Apollo Bluetooth
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

    private void handleActionPostBluetooth (String mac, Float dist, Integer btime, String uid){

        /*
        BluetoothDataInput bluetoothInput = bluetoothDataInput(mac, dist, btime);
        BluetoothDataListInput bluetoothInputList = bluetoothDataListInput(bluetoothInput, uid);

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
                });*/

        Log.v("DELIVERED BLUETOOTH", "[#] ApolloService.java - Bluetooth: " + mac
                    + " && " + dist + " && " + btime + " && " + uid);
    }
}

