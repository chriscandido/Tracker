package com.envisage.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.envisage.tracker.DashboardActivity;
import com.envisage.tracker.R;
import com.envisage.tracker.db.UserDBHandler;
import com.envisage.tracker.fragment.GoogleMapFragment;
import com.envisage.tracker.db.UserDBContactHelper;
import com.envisage.tracker.db.UserDBHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContactTracingActivity extends AppCompatActivity {

    Button selectDate;
    TextView date;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    LinearLayout linearLayout;

    // google maps button
    // redirects to the google maps page
    Button google_maps_btn, button_addContact, button_submit;
    EditText location_text, number_text;

    String contactLocation;
    String contactDate;
    String inputNum;

    UserDBHandler userDBHandler = new UserDBHandler(this);
    public List<String> contactNumbers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tracing);

        // date selection with calendar dialog
        selectDate = findViewById(R.id.select_date_btn);
        date = findViewById(R.id.date_selected);

        //Edite text number
        number_text = findViewById(R.id.editText_num);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ContactTracingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(dayOfMonth + "-" + month + "-" + year);
                                contactDate = String.valueOf(month + 1) + String.valueOf(dayOfMonth) + String.valueOf(year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_mobileNumber);

        // google maps button for pinning location
        google_maps_btn = findViewById(R.id.googlemap_btn);
        button_submit = findViewById(R.id.submit_btn);
        button_addContact = findViewById(R.id.add_contact_btn);

        onClick();

        location_text = findViewById(R.id.location_text);
        // get location data from google maps activity
        contactLocation = getIntent().getStringExtra("location_address");
        location_text.setText(contactLocation);

    }

    private void onClick() {
        google_maps_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startGoogleMaps = new Intent(ContactTracingActivity.this, GoogleMapFragment.class);
                        startActivity(startGoogleMaps);
                    }
                }
        );

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToDB(contactNumbers, contactLocation, contactDate);
                List<String> numbers = getDataFromDB();
                String address = userDBHandler.getLastContact().getAddress();
                String date = userDBHandler.getLastContact().getDate();
                Log.d("SERVER", "[#] ContactTracingActivity.java - Contacts: " + numbers +
                        " && " + " DATE " + date);
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        button_addContact.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getApplicationContext());

                /*
                editText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                editText.setHint("(Ex.9454728480)");
                editText.setId(0);
                Selection.setSelection(editText.getText(), editText.getText().length());
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().startsWith("+63")){
                            editText.setText("+63");
                            Selection.setSelection(editText.getText(), editText.getText().length());
                        }
                    }
                });

                linearLayout.addView(editText);*/

                contactNumbers.add(number_text.getText().toString());
                Log.v("CONTACT NUMBER", "[#] ContactTracingActivity.java - NUMBER: " + number_text.getText().toString());
            }
        });
    }

    public void insertToDB(List<String> contactNumber, String address, String date){
        Gson gson = new Gson();
        String inputContactNumber = gson.toJson(contactNumber);
        String userUniqueId = userDBHandler.getKeyUniqueid();
        userDBHandler.addContact(inputContactNumber, address, date, userUniqueId);
        Log.d("JSON ARRAY", "[#] ContactTracingActivity.java - Contacts: " + inputContactNumber
                + " && " + "DATE: " + date);
    }

    public List<String> getDataFromDB(){
        String contactNumber = userDBHandler.getLastContact().getNumber();
        String contactAddress =  userDBHandler.getLastContact().getAddress();
        String contactDate = userDBHandler.getLastContact().getDate();
        UserDBContactHelper userDBContactHelper = new UserDBContactHelper(contactNumber, contactAddress, contactDate);
        List<String> numberList = userDBContactHelper.getListString(contactNumber);
        return numberList;
    }
}
