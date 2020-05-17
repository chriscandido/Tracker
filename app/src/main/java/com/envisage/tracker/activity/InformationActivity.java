package com.envisage.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.envisage.tracker.R;

public class InformationActivity extends AppCompatActivity {

    Button button_purgeCovid, button_endCov, button_trams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        button_purgeCovid = findViewById(R.id.button_goToPurgeCovid);
        button_endCov = findViewById(R.id.button_goToEndCov);
        button_trams = findViewById(R.id.button_goToTrams);

        button_purgeCovid.setOnClickListener(new onClick());
        button_endCov.setOnClickListener(new onClick());
        button_trams.setOnClickListener(new onClick());
    }

    public class onClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_goToPurgeCovid:
                    goToUrl("https://purgecovid19-updge.hub.arcgis.com/");
                    break;
                case R.id.button_goToEndCov:
                    goToUrl("https://endcov.ph/dashboard/");
                    break;
                case R.id.button_goToTrams:
                    goToUrl("http://www.trams.com.ph/");
                    break;
            }
        }
    }

    public void goToUrl (String url){
        Uri uri = Uri.parse(url);
        Intent launch = new Intent(Intent.ACTION_VIEW, uri);
        launch.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(launch);
    }
}
