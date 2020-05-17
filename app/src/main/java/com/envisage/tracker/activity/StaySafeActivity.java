package com.envisage.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.envisage.tracker.R;


public class StaySafeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay_safe);

    }

    public void cancelActivity(View view) {
        finish();
    }
}
