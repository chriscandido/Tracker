package com.upd.contraplus2020.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.upd.contraplus2020.MainActivity;
import com.upd.contraplus2020.R;
import com.upd.contraplus2020.db.UserDBHandler;
import com.upd.contraplus2020.db.UserDBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ThreadLocalRandom;


public class LoginActivity extends AppCompatActivity{

    UserDBHandler userDBHandler = new UserDBHandler(this);
    UserDBHelper userDBHelper;

    private TextInputLayout text_inputNum;
    private TextInputEditText textInputEditText_inputNum;
    private Button button_subscribe, button_register;
    private String uid;
    private String number;
    private Integer random;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text_inputNum = findViewById(R.id.text_dateOfBirth);

        button_subscribe = findViewById(R.id.button_subscribe);
        button_register = findViewById(R.id.button_registerLogin);

        userDBHandler.initializeDB();

        initPreferences();
        onClickButtonListener();

    }

    public void initPreferences(){
        PreferenceActivity preferenceActivity = new PreferenceActivity();
        if (preferenceActivity.getUniqueId(this) != null){
            Log.v("Response", "[#] PreferenceActivity.java ");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void onClickButtonListener() {
        button_subscribe.setOnClickListener (
                    new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            number = text_inputNum.getEditText().getText().toString();
                            random = ThreadLocalRandom.current().nextInt(10000000, 99999999);
                            postDataToDatabase();
                            }

                    }
            );

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void postDataToDatabase(){
        String new_uid = userDBHandler.getKeyUniqueid();
        if (userDBHandler.checkUniqueId(new_uid)){
            PreferenceActivity.saveUniqueId(new_uid, this);
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            startActivity(intent);
            Log.d("LOGIN RESPONSE", "[#] LoginActivity.java - SUCCESS! UNIQUE ID added to Database: " + new_uid);
            finish();
        } else {
            Toast.makeText(context, "Register first before subscribing to the application", Toast.LENGTH_LONG).show();
        }
    }
}
