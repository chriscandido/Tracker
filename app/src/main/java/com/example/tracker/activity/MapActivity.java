package com.example.tracker.activity;

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
import android.widget.ImageButton;

import com.example.tracker.R;
import com.example.tracker.db.UserDBHandler;
import com.example.tracker.utils.Constants;
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
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class MapActivity extends AppCompatActivity {

    UserDBHandler userDBHandler = new UserDBHandler(this);

    ImageButton button_Hospital;

    public MapView mapView;
    public MapboxMap map;
    public Location location;
    public Style style;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, Constants.ACCESS_TOKEN);
        userDBHandler.initializeDB();

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        button_Hospital = findViewById(R.id.imagebutton_hospital);

        setMapView();

    }

    public void setMapView(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                map = mapboxMap;
                setCameraPosition(map);

                MarkerOptions options = new MarkerOptions();
                options.title("User Location");
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mapboxMap.addMarker(options);

                Log.i("GEOJSON", "[#] MapActivity.java GEOJSON: " );

                mapboxMap.setStyle(new Style.Builder().fromUri(Style.TRAFFIC_NIGHT)
                        .withImage("icon-layer-id", Objects.requireNonNull(BitmapUtils.getBitmapFromDrawable(
                                getResources().getDrawable(R.drawable.hospitalicon)
                        ))), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        new LoadGeoJson(MapActivity.this).execute();
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
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 15000);
        } else {
            Log.e("LOCATION", "[#] MapActivity - LOCATION is Empty" );
        }
    }
    public static Icon drawableToIcon(@NonNull Context context, @DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return IconFactory.getInstance(context).fromBitmap(bitmap);
    }

    private void postMarker(@NonNull final FeatureCollection featureCollection) {
        button_Hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    int coll = featureCollection.features().size();
                    List<Feature> features = featureCollection.features();
                    for (Feature feature : features){
                        if (feature.geometry() instanceof Point){
                            double latitude  = ((Point) feature.geometry()).latitude();
                            double longitude = ((Point) feature.geometry()).longitude();
                            String name = feature.getStringProperty("Name");

                            Icon icon = drawableToIcon(getApplicationContext(), R.mipmap.ic_hospitals);
                            MarkerOptions options = new MarkerOptions();
                            options.title(name);
                            options.position(new LatLng(latitude, longitude));
                            options.icon(icon);
                            map.addMarker(options);
                        }
                    }
                    Log.i("JSONObject", "[#] MapActivity.java - JSONObject: " + coll);
                }

            }
        });
    }

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
                    InputStream inputStream = mapActivity.getAssets().open("Hospitals.geojson");
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
