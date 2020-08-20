package com.upd.contraplus2020.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.upd.contraplus2020.db.UserDBHandler;

public class SplashActivity extends AppCompatActivity {

    UserDBHandler userDBHandler = new UserDBHandler(this);

    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initViews();
    }

    public void initViews(){
        /*if (userDBHandler.getKeyUniqueid().isEmpty()){
            Intent intent = new Intent(this, EulaActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SliderAdapterActivity.class);
            startActivity(intent);
        }*/
        Intent intent = new Intent(this, SliderAdapterActivity.class);
        startActivity(intent);
    }
}
