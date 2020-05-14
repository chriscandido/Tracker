package com.example.tracker.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tracker.AddUserMutation;
import com.example.tracker.MainActivity;
import com.example.tracker.R;
import com.example.tracker.db.UserDBHandler;
import com.example.tracker.service.ApolloClient;
import com.example.tracker.type.UserInput;
import com.example.tracker.utils.InputValidation;
import com.example.tracker.utils.UserDBHelper;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ThreadLocalRandom;

public class LoginActivity extends AppCompatActivity{

    UserDBHandler userDBHandler = new UserDBHandler(this);
    UserDBHelper userDBHelper;

    private TextInputLayout text_inputNum;
    private Button button_subscribe;
    private String uid;
    private String number;
    private Integer random;
    private Context context;

    private static final String SMS_SENT_INTENT_FILTER = "com.example.tracker.SMS_SEND";
    private static final String SMS_DELIVERED_INTENT_FILTER = "com.example.tracker.SMS_DELIVERED";
    private final static int SEND_SMS_PERMISSION_REQ=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text_inputNum = findViewById(R.id.text_dateOfBirth);
        button_subscribe = findViewById(R.id.button_subscribe);

        userDBHandler.initializeDB();

        initViews();
        onClickButtonListener();

    }

    public void initViews(){
        PreferenceActivity preferenceActivity = new PreferenceActivity();
        if (preferenceActivity.getUniqueId(this) != null){
            Log.v("Response", "[#] PreferenceActivity.java ");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("dateOfBirth", random);
            startActivity(intent);
        }
    }

    public UserInput addUserData(Integer input){
        UserInput userInput = UserInput.builder()
                .dateOfBirth(input)
                .build();
        return userInput;
    }

    private void onClickButtonListener() {
        if (checkPermission(Manifest.permission.SEND_SMS)){
            button_subscribe.setEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQ);
        }
        button_subscribe.setOnClickListener (
                    new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            number = text_inputNum.getEditText().getText().toString();
                            random = ThreadLocalRandom.current().nextInt(10000000, 99999999);
                            ApolloClient.setupApollo().mutate(AddUserMutation.builder().user(addUserData(random)).build())
                                .enqueue(new ApolloCall.Callback<AddUserMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<AddUserMutation.Data> response) {
                                        uid = response.data().addUser().id();
                                        postDataToDatabase(uid, random);
                                        sendSMS(number, uid);
                                    }
                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {
                                    }
                                });
                            }

                    }
            );
    }

    private boolean checkPermission(String sendSMS){
        int checkPermission = ContextCompat.checkSelfPermission(this, sendSMS);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void onRequestPermissionResult(int requestCode, @NotNull String[] permissions, @NonNull int[] grantResults){
        switch(requestCode){
            case SEND_SMS_PERMISSION_REQ:
                if (grantResults.length>0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    button_subscribe.setEnabled(true);
                }
                break;
        }
    }

    private void sendSMS(String number, String uid){
        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(uid)){
            if (checkPermission(Manifest.permission.SEND_SMS)) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number,null, "Your UNIQUE ID is " + userDBHandler.getKeyUniqueid(), null, null);
            } else {
                Toast.makeText(context, "[#] LoginActivity.java - Permission Denied", Toast.LENGTH_LONG).show();
            }
        }  else {
            Toast.makeText(context, "[#] LoginActivity.java - Permission Denied", Toast.LENGTH_SHORT).show();
        }
        Log.v("SMS RESPONSE:", "[#] LoginActivity.java - UNIQUE ID: " + uid);
    }

    private void postDataToDatabase(String uid, Integer random){
        userDBHelper = new UserDBHelper(uid);
        userDBHandler.addUser(userDBHelper);
        String new_uid = userDBHandler.getKeyUniqueid();
        if (userDBHandler.checkUniqueId(new_uid)){
            PreferenceActivity.saveUniqueId(new_uid, this);
            Intent intent = new Intent (getApplicationContext(), MainActivity.class);
            intent.putExtra("dateOfBirth", random);
            startActivity(intent);
            Log.d("LOGIN RESPONSE", "[#] LoginActivity.java - SUCCESS! UNIQUE ID added to Database: " + new_uid);
            finish();
        }
    }
}
