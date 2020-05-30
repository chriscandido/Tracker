package com.envisage.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.envisage.tracker.R;
import com.envisage.tracker.db.UserDBHandler;
import com.envisage.tracker.db.UserDBHelper;
import com.envisage.tracker.service.ApolloClient;
import com.example.tracker.AddUserMutation;
import com.example.tracker.type.UserInput;
import com.example.tracker.type._UserProfileInput;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout firstName, lastName, age, contactNumber, permanentAddress, permanentMunOrCity, presentAddress, presentMunOrCity;
    RadioButton male, female, others, positive, negative, waiting, none;
    CheckBox sameAsPermanentAddress, pum, pui;
    Button button_register;

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
        setContentView(R.layout.activity_register);

        userDBHandler.initializeDB();

        initViews();

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateContactNumber()) {
                    return;
                } else {
                    insertToServer();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Log.v("SUBMIT", "[#] RegisterActivity.java - UNIQUE ID: " + userDBHandler.getKeyUniqueid());
                }
            }
        });

    }

    private void initViews () {
        firstName = findViewById(R.id.textInputLayout_registerFirstname);
        lastName = findViewById(R.id.textInputLayout_registerLastname);
        age = findViewById(R.id.textInputLayout_registerAge);
        contactNumber = findViewById(R.id.textInputLayout_registerContactNum);
        permanentAddress = findViewById(R.id.textInputLayout_registerPermanent_Brgy);
        permanentMunOrCity = findViewById(R.id.textInputLayout_registerPermanent_MunOrCity);
        presentAddress = findViewById(R.id.textInputLayout_registerPresent_Brgy);
        presentMunOrCity = findViewById(R.id.textInputLayout_registerPresent_MunOrCity);

        male = findViewById(R.id.radioButton_registerSex_Male);
        female = findViewById(R.id.radioButton_registerSex_Female);
        others = findViewById(R.id.radioButton_registerSex_Others);

        positive = findViewById(R.id.radioButton_registerTestingResults_Positive);
        negative = findViewById(R.id.radioButton_registerTestingResults_Negative);
        waiting = findViewById(R.id.radioButton_registerTestingResults_Waiting);
        none = findViewById(R.id.radioButton_registerTestingResults_None);

        sameAsPermanentAddress = findViewById(R.id.checkBox_registerSameAsPermanent);
        pum = findViewById(R.id.checkBox_register_PUM);
        pui = findViewById(R.id.checkBox_register_PUI);

        pum.setOnCheckedChangeListener(new onCheckListener());
        pui.setOnCheckedChangeListener(new onCheckListener());

        button_register = findViewById(R.id.button_register);

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

    private void getGender(){
        if (male.isChecked()){
            db_gender = male.getText().toString();
        } else if (female.isChecked()) {
            db_gender = female.getText().toString();
        } else {
            db_gender = others.getText().toString();
        }
    }

    private void getTestResults() {
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

    private void getAddress() {
        if (sameAsPermanentAddress.isChecked()){
            db_presentAddress = db_permanentAddress;
            db_presentMunOrCity = db_permanentMunOrCity;

            presentAddress.setEnabled(false);
            presentMunOrCity.setEnabled(false);
        }
    }

    private boolean intToBoolean (Integer input) {
        if (input == 1) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean validateContactNumber () {
        String val = contactNumber.getEditText().getText().toString();

        if (val.isEmpty()) {
            contactNumber.setError("Field is required");
            return false;
        } else {
            contactNumber.setError(null);
            contactNumber.setErrorEnabled(false);
            return true;
        }
    }

    //----------------------------------------------------------------------------------------------add to local db
    private void insertToDB (String db_uniqueId) {
        userDBHelper = new UserDBHelper(db_uniqueId,
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

        userDBHandler.addUser(userDBHelper);

    }

    //----------------------------------------------------------------------------------------------add to server db

    public UserInput addUserData (_UserProfileInput profileInput){
        UserInput userInput = UserInput.builder()
                .profile(profileInput)
                .build();
        return userInput;
    }


    public _UserProfileInput profileInput (){

        db_firstName = firstName.getEditText().getText().toString();
        db_lastName = lastName.getEditText().getText().toString();
        db_age = Integer.parseInt(age.getEditText().getText().toString());
        db_contactNumber = contactNumber.getEditText().getText().toString();
        db_permanentAddress = permanentAddress.getEditText().getText().toString();
        db_permanentMunOrCity = permanentMunOrCity.getEditText().getText().toString();
        db_presentAddress = presentAddress.getEditText().getText().toString();
        db_presentMunOrCity = presentMunOrCity.getEditText().getText().toString();

        getAddress();
        getGender();
        getTestResults();
        
        _UserProfileInput userProfileInput = _UserProfileInput.builder()
                .firstNameInput(Input.optional(db_firstName))
                .lastNameInput(Input.optional(db_lastName))
                .ageInput(Input.optional(db_age))
                .contactNumberInput(Input.optional(db_contactNumber))
                .permanentAddressInput(Input.optional(db_permanentAddress))
                .municipalityOrCityInput(Input.optional(db_permanentMunOrCity))
                .presentAddressInput(Input.optional(db_presentAddress))
                .presentMunicipalityOrCityInput(Input.optional(db_presentMunOrCity))
                .isSuspectedOfContactInput(Input.optional(intToBoolean(db_pum)))
                .isPUIInput(Input.optional(intToBoolean(db_pui)))
                .genderInput(Input.optional(db_gender))
                .testResultInput(Input.optional(db_testResult))
                .build();
        return userProfileInput;
    }

    public void insertToServer (){

        UserInput userInput = addUserData(profileInput());

        //insertToDB("c9d0ec09-a188-42a0-8b3f-fdfe383c7c46");

        ApolloClient.setupApollo().mutate(AddUserMutation.builder().user(userInput).build())
                .enqueue(new ApolloCall.Callback<AddUserMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<AddUserMutation.Data> response) {
                        assert response.data() != null;
                        String uid = response.data().addUser().id();
                        //insertToDB("c9d0ec09-a188-42a0-8b3f-fdfe383c7c46");
                        insertToDB(uid);
                        Log.v("SERVER RESPONSE", "[#] RegisterActivity.java - RESPONSE: " + response.data().toString());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.e("SERVER RESPONSE", "[#] RegisterActivity.java - ERROR: " + e);
                    }
                });
        Log.v("RESPONSE", "[#] RegisterActivity.java - DATA: " +
                db_firstName + " " + db_lastName + "\n" + db_age + "\n" + db_presentAddress + "\n"
                + db_pui + "\n" + db_pum + "\n");
    }
}
