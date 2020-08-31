package com.upd.contraplus2020.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.upd.contraplus2020.ProfileActivity;
import com.upd.contraplus2020.R;
import com.upd.contraplus2020.db.UserDBHandler;
import com.upd.contraplus2020.db.UserDBHelper;
import com.upd.contraplus2020.service.ApolloClient;
import com.example.tracker.UpdateUserProfileMutation;
import com.example.tracker.type.UserProfileInput;
import com.example.tracker.type._UserProfileInput;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

public class EditProfileActivity extends AppCompatActivity {

    TextInputLayout firstName, lastName, age, contactNumber, permanentAddress, permanentMunOrCity, presentAddress, presentMunOrCity;
    RadioButton male, female, others, positive, negative, waiting, none;
    CheckBox sameAsPermanentAddress, pum, pui;
    Button button_edit;

    UserDBHelper userDBHelper;
    UserDBHandler userDBHandler = new UserDBHandler(this);

    Integer db_age = 0;
    Integer db_pum = 0;
    Integer db_pui = 0;
    String db_firstName = "";
    String db_lastName = "";
    String db_contactNumber = "";
    String db_permanentAddress = "";
    String db_permanentMunOrCity = "";
    String db_presentAddress = "";
    String db_presentMunOrCity = "";
    String db_gender = "";
    String db_testResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userDBHandler.initializeDB();

        initViews();

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDB();
                updateServer();
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                Log.v("SUBMIT", "[#] EditProfileActivity.java - UNIQUE ID: " + userDBHandler.getKeyUniqueid());
                }
            });
    }

    //----------------------------------------------------------------------------------------------Initialize variables
    private void initViews () {
        firstName = findViewById(R.id.textInputLayout_profileFirstname);
        lastName = findViewById(R.id.textInputLayout_profileLastname);
        age = findViewById(R.id.textInputLayout_profileAge);
        contactNumber = findViewById(R.id.textInputLayout_profileContactNum);
        permanentAddress = findViewById(R.id.textInputLayout_profilePermanent_Brgy);
        permanentMunOrCity = findViewById(R.id.textInputLayout_profilePermanent_MunOrCity);
        presentAddress = findViewById(R.id.textInputLayout_profilePresent_Brgy);
        presentMunOrCity = findViewById(R.id.textInputLayout_profilePresent_MunOrCity);

        male = findViewById(R.id.radioButton_profileSex_Male);
        female = findViewById(R.id.radioButton_profileSex_Female);
        others = findViewById(R.id.radioButton_profileSex_Others);

        positive = findViewById(R.id.radioButton_profileTestingResults_Positive);
        negative = findViewById(R.id.radioButton_profileTestingResults_Negative);
        waiting = findViewById(R.id.radioButton_profileTestingResults_Waiting);
        none = findViewById(R.id.radioButton_profileTestingResults_None);

        sameAsPermanentAddress = findViewById(R.id.checkBox_profileSameAsPermanent);
        pum = findViewById(R.id.checkBox_profile_PUM);
        pui = findViewById(R.id.checkBox_profile_PUI);

        pum.setOnCheckedChangeListener(new onCheckListener());
        pui.setOnCheckedChangeListener(new onCheckListener());

        button_edit = findViewById(R.id.button_edit);

    }

    private class onCheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                db_pum = 1;
                db_pui = 1;
            } else {
                db_pum = 0;
                db_pui = 0;
            }
        }
    }

    //----------------------------------------------------------------------------------------------Update personal details of user
    private void setDetails(){
        //Firstname
        String temp_firstName = firstName.getEditText().getText().toString();
        if (temp_firstName.isEmpty()){
            db_firstName = userDBHandler.getLastUserProfile().getFirstName();
        } else {
            db_firstName = firstName.getEditText().getText().toString();
        }


        //Lastname
        String temp_lastName = lastName.getEditText().getText().toString();
        if (temp_lastName.isEmpty()) {
            db_lastName = userDBHandler.getLastUserProfile().getLastName();
        } else {
            db_lastName = lastName.getEditText().getText().toString();
        }

        //Age
        String temp_age = age.getEditText().getText().toString();
        if (temp_age.isEmpty()){
            db_age = userDBHandler.getLastUserProfile().getAge();
        } else {
            db_age = Integer.parseInt(age.getEditText().getText().toString());
        }

        //Contact Number
        String temp_contactNumber = contactNumber.getEditText().getText().toString();
        if (temp_contactNumber.isEmpty()) {
            db_contactNumber = userDBHandler.getLastUserProfile().getContactNumber();
        } else {
            db_contactNumber = contactNumber.getEditText().getText().toString();
        }

        //Permanent Address
        String temp_permanentAddress = permanentAddress.getEditText().getText().toString();
        if (temp_permanentAddress.isEmpty()) {
            db_permanentAddress = userDBHandler.getLastUserProfile().getPermanentAddress();
        } else {
            db_permanentAddress = permanentAddress.getEditText().getText().toString();
        }

        //Permanent Municipality or City
        String temp_permanentMunOrCity = permanentMunOrCity.getEditText().getText().toString();
        if (temp_permanentMunOrCity.isEmpty()) {
            db_permanentMunOrCity = userDBHandler.getLastUserProfile().getMunicipalityOrCity();
        } else {
            db_permanentMunOrCity = permanentMunOrCity.getEditText().getText().toString();
        }

        //Present Address
        String temp_presentAddress = presentAddress.getEditText().getText().toString();
        if (temp_presentAddress.isEmpty()) {
            db_presentAddress = userDBHandler.getLastUserProfile().getPresentAddress();
        } else {
            db_presentAddress = presentAddress.getEditText().getText().toString();
        }

        //Present Municipality or City
        String temp_presentMunOrCity = presentMunOrCity.getEditText().getText().toString();
        if (temp_presentMunOrCity.isEmpty()) {
            db_presentMunOrCity = userDBHandler.getLastUserProfile().getPresent_municipalityOrCity();
        } else {
            db_presentMunOrCity = presentMunOrCity.getEditText().getText().toString();
        }
    }

    private void setGender(){
        if (male.isChecked()){
            db_gender = male.getText().toString();
        } else if (female.isChecked()) {
            db_gender = female.getText().toString();
        } else {
            db_gender = others.getText().toString();
        }
    }

    private void setTestResults() {
        if (positive.isChecked()){
            db_testResult = positive.getText().toString();
        } else if (negative.isChecked()) {
            db_testResult = negative.getText().toString();
        } else if (waiting.isChecked()) {
            db_testResult = waiting.getText().toString();
        } else {
            db_testResult = none.getText().toString();
        }
    }

    private void setAddress() {
        if (sameAsPermanentAddress.isChecked()){
            db_presentAddress = db_permanentAddress;
            db_presentMunOrCity = db_permanentMunOrCity;

            presentAddress.setEnabled(false);
            presentMunOrCity.setEnabled(false);
        }
    }

    public boolean intToBoolean(Integer input) {
        if (input == 1) {
            return true;
        } else {
            return false;
        }
    }


    private void updateUserDB() {
        setDetails();
        setAddress();
        setGender();
        setTestResults();

        String lastID = String.valueOf(userDBHandler.getLastUserId());

        userDBHelper = new UserDBHelper(userDBHandler.getKeyUniqueid(),
                db_firstName,
                db_lastName,
                db_age,
                db_contactNumber,
                db_permanentAddress,
                db_permanentMunOrCity,
                db_presentAddress,
                db_presentMunOrCity,
                db_pum,
                db_pui,
                db_gender,
                db_testResult);

        userDBHandler.updateUser(userDBHelper, lastID);
    }

    public UserProfileInput updateUserProfile (String id, _UserProfileInput _userProfileInput) {
        UserProfileInput userProfileInput = UserProfileInput.builder()
                .ownerId(id)
                .profile(_userProfileInput)
                .build();
        return  userProfileInput;
    }

    public _UserProfileInput profileInput () {

        setDetails();
        setAddress();
        setGender();
        setTestResults();

        _UserProfileInput userProfileInput = _UserProfileInput.builder()
                .firstName(db_firstName)
                .lastName(db_lastName)
                .age(db_age)
                .contactNumber(db_contactNumber)
                .permanentAddress(db_permanentAddress)
                .municipalityOrCity(db_permanentMunOrCity)
                .presentAddress(db_presentAddress)
                .presentMunicipalityOrCity(db_presentMunOrCity)
                .isSuspectedOfContact(intToBoolean(db_pum))
                .isPUI(intToBoolean(db_pui))
                .gender(db_gender)
                .testResult(db_testResult)
                .build();

        return userProfileInput;
    }

    //----------------------------------------------------------------------------------------------Update server
    public void updateServer (){

        String uniqueid = userDBHandler.getKeyUniqueid();
        UserProfileInput userProfileInput = updateUserProfile(uniqueid, profileInput());

        ApolloClient.setupApollo().mutate(UpdateUserProfileMutation.builder().user(userProfileInput).build())
                .enqueue(new ApolloCall.Callback<UpdateUserProfileMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserProfileMutation.Data> response) {
                        Log.v("SERVER RESPONSE", "[#] EditProfileActivity.java - RESPONSE: " + response.data().toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("SERVER RESPONSE", "[#] EditProfileActivity.java - ERROR: " + e);

                    }
                });
    }


}
