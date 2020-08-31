package com.upd.contraplus2020;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.upd.contraplus2020.activity.ManualTracingActivity;
import com.upd.contraplus2020.activity.InformationActivity;
import com.upd.contraplus2020.activity.MapActivity;
import com.upd.contraplus2020.activity.StaySafeActivity;
import com.upd.contraplus2020.activity.UpdateSymptomsActivity;

public class DashboardActivity extends AppCompatActivity {

    CardView cardView_staySafe, cardView_mapActivity, cardView_updateSymptoms;
    NestedScrollView nestedScrollView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        cardView_staySafe = findViewById(R.id.cardView_protectYourself);
        cardView_mapActivity = findViewById(R.id.cardView_Visualize);
        cardView_updateSymptoms = findViewById(R.id.cardView_updateSymptoms);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        cardView_staySafe.setOnTouchListener(new onTouch());
        cardView_mapActivity.setOnTouchListener(new onTouch());
        cardView_updateSymptoms.setOnTouchListener(new onTouch());
    }

    //----------------------------------------------------------------------------------------------Click animation
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

    public void goToContactTracing(View view){
        Intent intent = new Intent(this, ManualTracingActivity.class);
        startActivity(intent);
    }

    public void goToInformation(View view){
        Intent intent = new Intent(this, InformationActivity.class);
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
