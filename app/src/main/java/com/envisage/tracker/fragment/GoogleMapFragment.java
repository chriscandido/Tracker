package com.envisage.tracker.fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.envisage.tracker.R;
import com.envisage.tracker.activity.ContactTracingActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapFragment<AutocompleteSupportFragment>
        extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    Button submitLocationBtn;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final com.google.android.gms.maps.model.LatLng[] pinnedLocation = new com.google.android.gms.maps.model.LatLng[1];
        final String[] address_string = new String[1];


        // Add a marker in Philippines, move the camera and set zoom level
        final com.google.android.gms.maps.model.LatLng[] philippines = {new com.google.android.gms.maps.model.LatLng(12.65017682702677, 122.54032239317894)};
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philippines[0], 6.0f));

        // Add on map long click listener
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        mMap.clear();
                        Marker marker;
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .draggable(true));
                        pinnedLocation[0] = latLng;
                    }
                }
        );
        // TODO: OnMarkerClickListener
/*        mMap.setOnMarkerClickListener(
                new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        LatLng latLng = marker.getPosition();

                        //reverse geocoder to show address of pinned location
                        Geocoder geocoder = new Geocoder(GoogleMapFragment.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address_string = String.valueOf(addresses.get(0).getAddressLine(0));
                        //String address_string = String.valueOf(addresses.get(0));

                        Intent submitLocationAddressIntent = new Intent(GoogleMapFragment.this, ContactTracing.class);
                        submitLocationAddressIntent.putExtra("location_address", address_string);
                        startActivity(submitLocationAddressIntent);

                        return false;
                    }
                }
        );*/

        // view my location on map
        // TODO: request for location permission from user
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(GoogleMapFragment.this);
        mMap.setOnMyLocationClickListener(GoogleMapFragment.this);

        // TODO: create search bar with autocomplete using google places
        Places.initialize(getApplicationContext(), "AIzaSyAAEcTYPqxAEeO9zFcaQgcjY08xMuUneU8");
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment
        /*AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Log.e("AUTOCOMPLETE_FRAGMENT", String.valueOf(Places.isInitialized()));*/

        // Specify the types of place data to return

        // Set up a PlaceSelectionListener to handle the response



        // TODO: Create a SUBMIT LOCATION button
        submitLocationBtn = findViewById(R.id.submit_location_btn);
        submitLocationBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // reverse geocoder to show address of pinned location
                        if (pinnedLocation[0] != null) {
                            Geocoder geocoder = new Geocoder(GoogleMapFragment.this, Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(pinnedLocation[0].latitude, pinnedLocation[0].longitude, 1);
                                if (addresses != null) {
                                    try {
                                        address_string[0] = String.valueOf(addresses.get(0).getAddressLine(0));
                                    }
                                    catch (IndexOutOfBoundsException e) {
                                        address_string[0] = "NO ADDRESS LINE MATCHED";
                                    }
                                } else {
                                    address_string[0] = "NO ADDRESS MATCHES WERE FOUND";
                                }

                                Intent submitLocationIntent = new Intent(GoogleMapFragment.this, ContactTracingActivity.class);
                                submitLocationIntent.putExtra("location_address", address_string[0]);
                                startActivity(submitLocationIntent);

                            } catch (IOException e) {
                                //e.printStackTrace();
                                // open dialog box
                                openGeocoderFailedDialog();
                            }

                            //Intent submitLocationIntent = new Intent(GoogleMapFragment.this, ContactTracing.class);
                            //submitLocationIntent.putExtra("location_address", address_string[0]);
                            //startActivity(submitLocationIntent);
                        } else {
                            Log.e("GEOCODER SERVICE", "[#] GoogleMapsActivity.java - NO PIN WAS FOUND!");
                        }
                    }
                }
        );

    }

    /** Access my location and zoom in on the map*/
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void openGeocoderFailedDialog() {
        GeocoderFailedFragment geocoderFailedFragment = new GeocoderFailedFragment();
        geocoderFailedFragment.show(getSupportFragmentManager(), "Geocoder Failed");
    }
}
