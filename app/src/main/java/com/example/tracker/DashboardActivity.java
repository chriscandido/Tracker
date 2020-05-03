package com.example.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.tracker.activity.MapActivity;
import com.example.tracker.activity.StaySafeActivity;
import com.example.tracker.activity.UpdateSymptomsActivity;

public class DashboardActivity extends AppCompatActivity {

    ImageButton button_staySafe, button_mapActivity, button_updateSymptoms;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        button_staySafe = findViewById(R.id.protection_btn);
        button_mapActivity = findViewById(R.id.maps_btn);
        button_updateSymptoms = findViewById(R.id.symptoms_btn);

        button_staySafe.setOnTouchListener(new onTouch());
        button_mapActivity.setOnTouchListener(new onTouch());
        button_updateSymptoms.setOnTouchListener(new onTouch());
    }

    public class onTouch implements View.OnTouchListener{
        @Override
        public boolean onTouch(View view, MotionEvent event){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN: {
                    view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                    break;
                }
            }
            return false;
        }

    }

    public void goToStaySafe(View view){
        Intent intent = new Intent(this, StaySafeActivity.class);
        startActivity(intent);
    }

    public void goToMapActivity(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void gotToUpdateSymptoms(View view){
        Intent intent = new Intent(this, UpdateSymptomsActivity.class);
        startActivity(intent);
    }

    public void goToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
