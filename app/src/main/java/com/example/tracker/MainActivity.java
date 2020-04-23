package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillId;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tracker.service.ApolloClient;
import com.example.tracker.service.LocationService;
import com.example.tracker.type.LocationInput;
import com.example.tracker.type.LocationListInput;
import com.example.tracker.type.UserInput;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_ID = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //apolloConnector();
    }

    public void startService(View view){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        startService(new Intent(getBaseContext(), LocationService.class));
    }

    public UserInput addUserData(Integer input){
        UserInput userInput = UserInput.builder()
                .dateOfBirth(input)
                .build();
        return userInput;
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

    public void apolloConnector(){
        ApolloClient.setupApollo().mutate(AddUserMutation.builder().user(addUserData(504921610)).build())
                .enqueue(new ApolloCall.Callback<AddUserMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<AddUserMutation.Data> response) {
                        AddUserMutation.Data data = response.data();
                        Log.v("Response: ", data.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

        Double lat = 14.6529735;
        Double lon = 121.0561203;
        Integer n = 1586823537;
        String id = "f7aab801-21b2-46af-9b7f-895dadd91187";

        LocationInput loc = locationInput(lat, lon, n);
        LocationListInput locList = locationInputs(loc, id);

        ApolloClient.setupApollo().mutate(UpdateLocationHistoryMutation.builder().input(locList).build())
                .enqueue(new ApolloCall.Callback<UpdateLocationHistoryMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateLocationHistoryMutation.Data> response) {
                        UpdateLocationHistoryMutation.Data data = response.data();
                        Log.v("Response: ", data.toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }

    public void stopService(View view){
        stopService(new Intent(getBaseContext(), LocationService.class));
    }
}
