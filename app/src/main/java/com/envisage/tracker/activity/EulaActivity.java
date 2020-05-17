package com.envisage.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.envisage.tracker.R;

public class EulaActivity extends AppCompatActivity {

    CheckBox checkbox_agreement;
    Button button_agreement;
    Integer checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        checkbox_agreement = (CheckBox) findViewById(R.id.checkbox_agreement);
        button_agreement = findViewById(R.id.button_agreement);

        checkbox_agreement.setOnCheckedChangeListener(new onCheckListener());

        isFirstOpen();

    }

    public class onCheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                checked = 1;
                button_agreement.setVisibility(View.VISIBLE);
            } else {
                checked = 0;
                button_agreement.setVisibility(View.GONE);
            }
        }
    }

    private void isFirstOpen(){
        Boolean isFirstRun = getSharedPreferences("Agreement", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (!isFirstRun){
            Intent intent = new Intent(EulaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        getSharedPreferences("Agreement", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
