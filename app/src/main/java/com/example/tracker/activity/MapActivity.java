package com.example.tracker.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.tracker.R;
import com.example.tracker.db.UserDBHandler;
import com.example.tracker.utils.Constants;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class MapActivity extends AppCompatActivity {

    UserDBHandler userDBHandler = new UserDBHandler(this);

    public MapView mapView;
    public MapboxMap map;
    public Location location;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, Constants.ACCESS_TOKEN);
        userDBHandler.initializeDB();

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        setMapView();

    }

    public void setMapView(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                    setCameraPosition(mapboxMap);
                    MarkerOptions options = new MarkerOptions();
                    options.title("User Location");
                    options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    mapboxMap.addMarker(options);
                    mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                    }
                });
            }
        });
    }

    public void setCameraPosition(MapboxMap mapboxMap){
        location = userDBHandler.getLastLocation().getLocation();
        if (location != null) {
            Log.d("LOCATION", "[#] MapActivity - LOCATION: " + location.getLatitude() +
                    " && " + location.getLongitude());
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                    .zoom(17) // Sets the zoom
                    .bearing(180) // Rotate the camera
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 8000);
        } else {
            Log.e("LOCATION", "[#] MapActivity - LOCATION is Empty" );
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    protected void onSavedInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
