package com.example.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tracker.ContactTracing;
import com.example.tracker.DashboardActivity;
import com.example.tracker.R;
import com.example.tracker.db.UserDBHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateSymptomsActivity extends AppCompatActivity {

    CheckBox severeRespiratoryInfection,pneumonia,fever,diarrhea,bodyAches,tiredness,runnyNose,
            difficultyInBreathing, lossOfSmell,lossOfTaste;

    Button button_Submit, button_Back, button_selectDate;

    TextView textview_Date;
    EditText otherSymptoms;

    DatePickerDialog datePickerDialog;

    Calendar calendar;

    Integer checked;
    Integer year;
    Integer month;
    Integer dayOfMonth;
    Integer date;

    UserDBHandler userDBHandler = new UserDBHandler(this);
    public List<String> userSymptoms = new ArrayList<String>();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_symptoms);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        severeRespiratoryInfection = (CheckBox) findViewById(R.id.symptoms_severerespiratoryinfection);
        pneumonia = (CheckBox) findViewById(R.id.symptoms_pneumonia);
        fever = (CheckBox) findViewById(R.id.symptoms_fever);
        diarrhea = (CheckBox) findViewById(R.id.symptoms_diarrhea);
        bodyAches = (CheckBox) findViewById(R.id.symptoms_bodyaches);
        tiredness = (CheckBox) findViewById(R.id.symptoms_tiredness);
        runnyNose = (CheckBox) findViewById(R.id.symptoms_runnynose);
        difficultyInBreathing = (CheckBox) findViewById(R.id.symptoms_difficultyinbreathing);
        lossOfSmell = (CheckBox) findViewById(R.id.symptoms_lossofsmell);
        lossOfTaste = (CheckBox) findViewById(R.id.symptoms_lossoftaste);

        severeRespiratoryInfection.setOnCheckedChangeListener(new onCheckListener());
        pneumonia.setOnCheckedChangeListener(new onCheckListener());
        fever.setOnCheckedChangeListener(new onCheckListener());
        diarrhea.setOnCheckedChangeListener(new onCheckListener());
        bodyAches.setOnCheckedChangeListener(new onCheckListener());
        tiredness.setOnCheckedChangeListener(new onCheckListener());
        runnyNose.setOnCheckedChangeListener(new onCheckListener());
        difficultyInBreathing.setOnCheckedChangeListener(new onCheckListener());
        lossOfSmell.setOnCheckedChangeListener(new onCheckListener());
        lossOfTaste.setOnCheckedChangeListener(new onCheckListener());

        button_Submit = findViewById(R.id.symptoms_submit);
        button_Back = findViewById(R.id.symptoms_cancel);
        button_selectDate = findViewById(R.id.select_date_btn);

        button_Submit.setOnClickListener(new onClick());
        button_Back.setOnClickListener(new onClick());
        button_selectDate.setOnClickListener(new onClick());

        textview_Date = findViewById(R.id.date_selected);

        button_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(UpdateSymptomsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                textview_Date.setText(month + 1 + "-" + dayOfMonth + "-" + year);
                                date = Integer.parseInt(String.valueOf(month + 1) + String.valueOf(dayOfMonth) + String.valueOf(year));
                                Log.d("DATE", "[#] UpdateSymptomsActivity.java - DATE: " + date);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

    }

    public class onCheckListener implements CompoundButton.OnCheckedChangeListener {
       @Override
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if (isChecked){
               checked = 1;
               userSymptoms.add(buttonView.getText().toString());
               Log.d("CHECKBOX", "[#] UpdateSymptomsActivity.java - SYMPTOMS: " + buttonView.getText().toString());
           } else {
               checked = 0;
           }
       }
   }

   public class onClick implements View.OnClickListener {
       @Override
       public void onClick(View v) {
           switch (v.getId()){
               case R.id.symptoms_submit:
                   insertToDB(userSymptoms, date);
                   goToDashboard();
                   break;
               case R.id.symptoms_cancel:
                   Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                   startActivity(intent);
                   break;
           }
       }
   }

   public void insertToDB(List<String> userSymptoms, Integer time){
        Gson gson = new Gson();
        String inputSymptoms = gson.toJson(userSymptoms);
        userDBHandler.addSymptoms(inputSymptoms,time);
        Log.d("JSON ARRAY", "[#] UpdateSymptomsActivity.java - SYMPTOMS: " + inputSymptoms
        + " && " + "DATE: " + date);
   }

   public void goToDashboard(){
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
   }
}
