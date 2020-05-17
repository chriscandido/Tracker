package com.envisage.tracker.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class GeocoderFailedFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Geocoder Failed")
                .setPositiveButton("SUBMIT ANYWAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*Intent submitLocationAnywayIntent = new Intent(GeocoderFailedFragment.this, ContactTracing.class);
                        submitLocationAnywayIntent.putExtra("location_address", "GEOCODER_FAILED");
                        startActivity(submitLocationAnywayIntent);*/
                    }
                })
                .setNegativeButton("RE-SELECT LOCATION FROM MAP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}