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

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.tracker.ContactTracing;
import com.example.tracker.DashboardActivity;
import com.example.tracker.R;
import com.example.tracker.UpdateLocationHistoryMutation;
import com.example.tracker.UpdateSymptomHistoryMutation;
import com.example.tracker.db.UserDBHandler;
import com.example.tracker.service.ApolloClient;
import com.example.tracker.type.SymptomsInput;
import com.example.tracker.type.SymptomsListInput;
import com.example.tracker.utils.UserDBSymptoms;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

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
                   List<String> symptoms = getDataFromDB();
                   Integer userDate = userDBHandler.getLastSymptoms().getDate();
                   String uniquedId = userDBHandler.getKeyUniqueid();
                   insertToServer(symptoms, userDate, uniquedId);
                   Log.d("SERVER", "[#] UpdateSymptomsActivity.java - SYMPTOMS: " + symptoms +
                   " && " + " DATE " + userDate);
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

   public List<String> getDataFromDB(){
        String userSymptoms =  userDBHandler.getLastSymptoms().getSymptoms();
        Integer userDate = userDBHandler.getLastSymptoms().getDate();
        UserDBSymptoms userDBSymptoms= new UserDBSymptoms(userSymptoms, userDate);
        List<String> symptomsList = userDBSymptoms.getListString(userSymptoms);
        return symptomsList;
   }

   //-----------------------------------------------------------------------------------------------Server Communication
   public SymptomsInput symptomsInput (List<String> userSymptoms, Integer createdAt){
        SymptomsInput symptomsInput = SymptomsInput.builder()
                .literal(userSymptoms)
                .createdAt(createdAt)
                .build();
       return symptomsInput;
   }

   public SymptomsListInput symptomsListInput(SymptomsInput symptomsInput, String id){
        List<SymptomsInput> symptomsInputs = new ArrayList<>();
        symptomsInputs.add(symptomsInput);
        SymptomsListInput symptomsListInput = SymptomsListInput.builder()
                .list(symptomsInputs)
                .ownerId(id)
                .build();
        return symptomsListInput;
   }

   public void insertToServer (List<String> userSymptoms, Integer createdAt, String uid){

        SymptomsInput symptomsInput = symptomsInput(userSymptoms, createdAt);
        SymptomsListInput symptomsListInput = symptomsListInput(symptomsInput, uid);

        ApolloClient.setupApollo().mutate(UpdateSymptomHistoryMutation.builder().input(symptomsListInput).build())
               .enqueue(new ApolloCall.Callback<UpdateSymptomHistoryMutation.Data>() {
                   @Override
                   public void onResponse(@NotNull Response<UpdateSymptomHistoryMutation.Data> response) {
                       UpdateSymptomHistoryMutation.Data data = response.data();
                       Log.v("SERVER: ", "[#] UpdateSymptomsActivity.java: " + data.toString());
                   }
                   @Override
                   public void onFailure(@NotNull ApolloException e) {

                   }
               });

       Log.d("DATABASE", "[#] UpdateSymptomsActivity.java - SYMPTOMS: " + userSymptoms +
               " && " + " DATE " + createdAt + "ADDED TO " + " UNIQUE ID: " + uid);

   }

   public void goToDashboard(){
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
   }
}
