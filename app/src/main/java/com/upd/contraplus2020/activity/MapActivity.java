package com.upd.contraplus2020.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tracker.UserLocationHistoryQuery;
import com.upd.contraplus2020.R;
import com.upd.contraplus2020.db.UserDBHandler;
import com.upd.contraplus2020.service.ApolloClient;
import com.upd.contraplus2020.utils.Constants;
import com.upd.contraplus2020.utils.HaversineFormula;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.upd.contraplus2020.utils.UserLatLng;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class MapActivity extends AppCompatActivity {

    UserDBHandler userDBHandler = new UserDBHandler(this);

    FloatingActionButton button_FloatingAction, button_Hospital, button_Clinic, button_Pharmacy, button_Route;

    public MapView mapView;
    public MapboxMap map;
    public Location location;
    public Style mStyle;

    Float translationY = 100f;
    Boolean isMenuOpen = false;
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

    private ArrayList<UserLatLng> latLngList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, Constants.ACCESS_TOKEN);
        userDBHandler.initializeDB();

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        initButton();
        setMapView();
    }

    public void initButton(){
        button_FloatingAction = findViewById(R.id.floating_action_button);
        button_Hospital = findViewById(R.id.floating_actionButton_hospital);
        button_Clinic = findViewById(R.id.floating_actionButton_Clinic);
        button_Pharmacy = findViewById(R.id.floating_actionButton_Pharmacy);
        button_Route = findViewById(R.id.floating_actionButton_routeHistory);

        button_Hospital.setAlpha(0f);
        button_Clinic.setAlpha(0f);
        button_Pharmacy.setAlpha(0f);
        button_Route.setAlpha(0f);

        button_Hospital.setTranslationY(translationY);
        button_Clinic.setTranslationY(translationY);
        button_Pharmacy.setTranslationY(translationY);
        button_Route.setTranslationY(translationY);

        button_FloatingAction.setOnClickListener(new onClick());
    }

    private void openMenu(){
        isMenuOpen = !isMenuOpen;
        button_FloatingAction.animate().setInterpolator(overshootInterpolator).rotation(45f).setDuration(300).start();
        button_Hospital.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Clinic.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Pharmacy.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Route.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;
        button_FloatingAction.animate().setInterpolator(overshootInterpolator).rotation(0f).setDuration(300).start();
        button_Hospital.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Clinic.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Pharmacy.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
        button_Route.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
    }

    public class onClick implements View.OnClickListener {
        public void onClick(View view) {
            if (view.getId() == R.id.floating_action_button) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        }
    }

    public void setMapView(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                setCameraPosition(map);

                Icon icon = drawableToIcon(getApplicationContext(), R.drawable.usermarker);

                MarkerOptions options = new MarkerOptions();
                options.title("User Location");
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                options.icon(icon);
                mapboxMap.addMarker(options);

                mapboxMap.setStyle(new Style.Builder().fromUri(Style.TRAFFIC_NIGHT)
                        .withImage("icon-layer-id", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.hospitalicon)
                        ))), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mStyle = style;
                        new LoadGeoJson(MapActivity.this).execute();
                    }
                });
            }
        });
    }

    //----------------------------------------------------------------------------------------------Set camera position
    public void setCameraPosition(MapboxMap mapboxMap){
        location = userDBHandler.getLastLocation().getLocation();
        if (location != null) {
            Log.d("LOCATION", "[#] MapActivity - LOCATION: " + location.getLatitude() +
                    " && " + location.getLongitude());
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                    .zoom(14) // Sets the zoom
                    .bearing(90) // Rotate the camera
                    .tilt(45) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 15000);
        } else {
            Log.e("LOCATION", "[#] MapActivity - LOCATION is Empty" );
        }
    }

    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    //----------------------------------------------------------------------------------------------Add marker to map
    private void postMarker(@NonNull final FeatureCollection featureCollection) {

        mStyle.addSource (new GeoJsonSource("Features", featureCollection, new GeoJsonOptions().withCluster(true)
        .withClusterMaxZoom(14).withClusterRadius(100))
        );

        button_Hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.removeAnnotations();
                if (map != null) {
                    assert featureCollection.features() != null;
                    int coll = featureCollection.features().size();
                    List<Feature> features = featureCollection.features();
                    for (Feature feature : features){
                        if (feature.geometry() instanceof Point){
                            if (feature.getStringProperty("amenity").equals("hospital")) {
                                double latitude = ((Point) feature.geometry()).latitude();
                                double longitude = ((Point) feature.geometry()).longitude();
                                String name = feature.getStringProperty("Name");
                                HaversineFormula haversineFormula = new HaversineFormula(
                                        userDBHandler.getLastLocation().getLatitude(),
                                        userDBHandler.getLastLocation().getLongitude(),
                                        latitude,
                                        longitude
                                );
                                double distance = haversineFormula.getDistance();
                                if (distance < 10) {
                                    Icon icon = drawableToIcon(getApplicationContext(), R.mipmap.ic_hospitals);
                                    MarkerOptions options = new MarkerOptions();
                                    options.title(name);
                                    options.position(new LatLng(latitude, longitude));
                                    options.icon(icon);
                                    map.addMarker(options);
                                }
                            }
                        }
                    }
                }
            }

        });

        button_Clinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.removeAnnotations();
                if (map != null) {
                    assert featureCollection.features() != null;
                    int coll = featureCollection.features().size();
                    List<Feature> features = featureCollection.features();
                    for (Feature feature : features){
                        if (feature.geometry() instanceof Point){
                            if (feature.getStringProperty("amenity").equals("clinic")) {
                                double latitude = ((Point) feature.geometry()).latitude();
                                double longitude = ((Point) feature.geometry()).longitude();
                                String name = feature.getStringProperty("Name");
                                HaversineFormula haversineFormula = new HaversineFormula(
                                        userDBHandler.getLastLocation().getLatitude(),
                                        userDBHandler.getLastLocation().getLongitude(),
                                        latitude,
                                        longitude
                                );
                                double distance = haversineFormula.getDistance();
                                if (distance < 10) {
                                    Icon icon = drawableToIcon(getApplicationContext(), R.mipmap.ic_clinics);
                                    MarkerOptions options = new MarkerOptions();
                                    options.title(name);
                                    options.position(new LatLng(latitude, longitude));
                                    options.icon(icon);
                                    map.addMarker(options);
                                }
                            }
                        }
                    }
                }
            }
        });



        button_Pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.removeAnnotations();
                if (map != null) {
                    assert featureCollection.features() != null;
                    int coll = featureCollection.features().size();
                    List<Feature> features = featureCollection.features();
                    for (Feature feature : features){
                        if (feature.geometry() instanceof Point){
                            if (feature.getStringProperty("amenity").equals("pharmacy")) {
                                double latitude = ((Point) feature.geometry()).latitude();
                                double longitude = ((Point) feature.geometry()).longitude();
                                String name = feature.getStringProperty("Name");
                                HaversineFormula haversineFormula = new HaversineFormula(
                                        userDBHandler.getLastLocation().getLatitude(),
                                        userDBHandler.getLastLocation().getLongitude(),
                                        latitude,
                                        longitude
                                );
                                double distance = haversineFormula.getDistance();
                                if (distance < 10) {
                                    Icon icon = drawableToIcon(getApplicationContext(), R.mipmap.ic_pharmacy);
                                    MarkerOptions options = new MarkerOptions();
                                    options.title(name);
                                    options.position(new LatLng(latitude, longitude));
                                    options.icon(icon);
                                    map.addMarker(options);
                                }
                            }
                        }
                    }
                }
            }
        });

        button_Route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.removeAnnotations();
                queryLocation();
                int coll = latLngList.size();
                /**int coll = queryLocation().size();**/
                for (int i = 0; i < coll; i++) {
                    double lat = latLngList.get(i).getLatitude();
                    double lon = latLngList.get(i).getLongitude();
                    int createdAt = latLngList.get(i).getCreatedAt();
                    Icon icon = drawableToIcon(getApplicationContext(), R.drawable.ic_userlocationpin);
                    MarkerOptions options = new MarkerOptions();
                    options.title(String.valueOf(createdAt));
                    options.position(new LatLng(lat, lon));
                    options.icon(icon);
                    map.addMarker(options);
                }
            }
        });

    }

    //----------------------------------------------------------------------------------------------Load GeoJson file from asset folder
    private static class LoadGeoJson extends AsyncTask<Void, Void, FeatureCollection> {

        private WeakReference<MapActivity> weakReference;

        LoadGeoJson(MapActivity mapActivity) {
            this.weakReference = new WeakReference<>(mapActivity);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected FeatureCollection doInBackground(Void... voids){
            try {
                MapActivity mapActivity = weakReference.get();
                if (mapActivity != null){
                    InputStream inputStream = mapActivity.getAssets().open("MedicalFacilities.geojson");
                    return FeatureCollection.fromJson(convertStreamtoString(inputStream));
                }
            } catch (Exception exception){
                Log.e("Exception Loading GeoJson: %s", exception.toString());
            }
            return null;
        }

        static String convertStreamtoString(InputStream inputStream){
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext()? scanner.next(): "";
        }

        protected void onPostExecute(@Nullable FeatureCollection featureCollection){
            super.onPostExecute(featureCollection);
            MapActivity mapActivity = weakReference.get();
            if (mapActivity != null && featureCollection != null){
               mapActivity.postMarker(featureCollection);
            }
        }
    }

    //----------------------------------------------------------------------------------------------Query location history
    public UserLocationHistoryQuery userId (String userId) {
        UserLocationHistoryQuery uniqueId = UserLocationHistoryQuery.builder()
                .userId(userId)
                .build();
        return uniqueId;
    }

    public void queryLocation(){

        UserLocationHistoryQuery uniqueId = userId(userDBHandler.getKeyUniqueid());

        ApolloClient.setupApollo().query(uniqueId).enqueue(new ApolloCall.Callback<UserLocationHistoryQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<UserLocationHistoryQuery.Data> response) {
                Log.v("SERVER", "[#] MapActivity.java: " + response.data().user().locationHistory().size());
                UserLatLng userLatLng;
                int coll = response.data().user().locationHistory().size();
                for (int i = 0; i < coll; i++){
                    double latitude = response.data().user().locationHistory().get(i).latitude();
                    double longitude = response.data().user().locationHistory().get(i).longitude();
                    int createdAt = response.data().user().locationHistory().get(i).createdAt();
                    userLatLng = new UserLatLng(latitude, longitude, createdAt);
                    latLngList.add(userLatLng);
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
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
