package com.upd.contraplus2020.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.upd.contraplus2020.R;


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
