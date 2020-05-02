package com.example.tracker.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tracker.UpdateLocationHistoryMutation;
import com.example.tracker.type.LocationInput;
import com.example.tracker.type.LocationListInput;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ApolloService extends IntentService {
    private final static String ACTION_POST_LOCATION = "com.example.tracker.service.action.POST_LOCATION";
    private final static String LAT = "com.example.tracker.service.extra.Latitude";
    private final static String LON = "com.example.tracker.service.extra.Longitude";
    private final static String TIME = "com.example.tracker.service.extra.dateTime";
    private final static String UID = "com.example.tracker.service.extra.Uid";
    private final static String TAG = "LOCATION SERVICE";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public ApolloService() {
        super("ApolloService");
    }

    public static void startActionPostLocation(Context context, Double lat, Double lon, Integer time, String uid){
        Intent intent = new Intent(context, ApolloService.class);
        intent.setAction(ACTION_POST_LOCATION);
        intent.putExtra(LAT, lat);
        intent.putExtra(LON, lon);
        intent.putExtra(TIME, time);
        intent.putExtra(UID, uid);
        context.startService(intent);
    }

    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_POST_LOCATION.equals(action)){
                final Double lat = intent.getDoubleExtra(LAT, 0.00);
                final Double lon = intent.getDoubleExtra(LON, 0.00);
                final Integer time = intent.getIntExtra(TIME, 0);
                final String uid = intent.getStringExtra(UID);
                handleActionPostLocation(lat, lon, time, uid);
            }
        } else {
            Log.v("LOCATION", "[#] ApolloService.java - Location: Null");
        }
    }

    public LocationInput locationInput(double lat, double lon, int createdat){
        LocationInput locationInput = LocationInput.builder()
                .latitude(lat)
                .longitude(lon)
                .createdAt(createdat)
                .build();
        return locationInput;
    }

    public LocationListInput locationInputs(LocationInput locationInputList, String id){
        List<LocationInput> locationInputs = new ArrayList<>();
        locationInputs.add(locationInputList);
        LocationListInput locationListInput = LocationListInput.builder()
                .list(locationInputs)
                .ownerId(id)
                .build();
        return locationListInput;
    }

    private void handleActionPostLocation(Double lat, Double lon, Integer time, String uid) {

        LocationInput loc = locationInput(lat, lon, time);
        LocationListInput locList = locationInputs(loc, uid);

        ApolloClient.setupApollo().mutate(UpdateLocationHistoryMutation.builder().input(locList).build())
                .enqueue(new ApolloCall.Callback<UpdateLocationHistoryMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateLocationHistoryMutation.Data> response) {
                        UpdateLocationHistoryMutation.Data data = response.data();
                        Log.v("SERVER: ", "[#] ApolloService.java: " + data.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                    }
                });

        Log.v("DELIVERED LOCATION", "[#] ApolloService.java - Location: " + lat
                + " && " + lon + " && " + time + " && " + uid);

    }
}
