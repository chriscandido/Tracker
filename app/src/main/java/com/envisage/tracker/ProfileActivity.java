package com.envisage.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.envisage.tracker.activity.EditProfileActivity;
import com.envisage.tracker.db.UserDBHandler;
import com.envisage.tracker.db.UserDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton_edit;

    TextView textView_name, textView_age, textView_gender, textView_mobileNumber, textView_presentAddress, textView_textResult;

    UserDBHandler userDBHandler = new UserDBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userDBHandler.initializeDB();

        floatingActionButton_edit = findViewById(R.id.floating_action_buttonEdit);
        floatingActionButton_edit.setOnClickListener(new onClick());


        textView_name = findViewById(R.id.textView_ProfileCover_Name);
        textView_age = findViewById(R.id.textView_ProfileCover_LabelAge);
        textView_gender = findViewById(R.id.textView_ProfileCover_LabelGender);
        textView_mobileNumber = findViewById(R.id.textView_ProfileCover_LabelMobileNumber);
        textView_presentAddress = findViewById(R.id.textView_ProfileCover_LabelPresentAddress);
        textView_textResult = findViewById(R.id.textView_ProfileCover_LabelTestResult);

        initViews();

    }

    public void initViews() {
        String fullName;
        if (userDBHandler.getLastUserProfile().getFirstName().isEmpty() && userDBHandler.getLastUserProfile().getLastName().isEmpty()) {
            fullName = "N/A";
            textView_name.setText(fullName);
        } else {
            fullName = userDBHandler.getLastUserProfile().getFirstName() + " " + userDBHandler.getLastUserProfile().getLastName();
            textView_name.setText(fullName);
        }

        String age;
        if (userDBHandler.getLastUserProfile().getAge().toString().isEmpty()) {
            age = "N/A";
            textView_age.setText(age);
        } else {
            age = userDBHandler.getLastUserProfile().getAge().toString() + " years old";
            textView_age.setText(age);
        }

        String gender;
        if (userDBHandler.getLastUserProfile().getGender().isEmpty()) {
            gender = "N/A";
            textView_gender.setText(gender);
        } else {
            gender = userDBHandler.getLastUserProfile().getGender();
            textView_gender.setText(gender);
        }

        String mobileNumber;
        if (userDBHandler.getLastUserProfile().getContactNumber().isEmpty()){
            mobileNumber = "N/A";
            textView_mobileNumber.setText(mobileNumber);
        } else {
            mobileNumber = userDBHandler.getLastUserProfile().getContactNumber();
            textView_mobileNumber.setText(mobileNumber);
        }

        String address;
        if (userDBHandler.getLastUserProfile().getPresentAddress().isEmpty()) {
            address = "N/A";
            textView_presentAddress.setText(address);
        } else {
            address = userDBHandler.getLastUserProfile().getPresentAddress() + "," + userDBHandler.getLastUserProfile().getPresent_municipalityOrCity();
            textView_presentAddress.setText(address);
        }

        String testResult;
        if (userDBHandler.getLastUserProfile().getTestResult().isEmpty()) {
            testResult = "CONFIDENTIAL";
            textView_textResult.setText(testResult);
        } else {
            testResult = userDBHandler.getLastUserProfile().getTestResult();
            textView_textResult.setText(testResult);
        }

    }

    public class onClick implements View.OnClickListener {
        public void onClick(View view) {
            if (view.getId() == R.id.floating_action_buttonEdit) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                Log.v("SAMPLE DATA", "DATA: " + userDBHandler.getLastUserProfile().getTestResult());
            }
        }
    }

}
