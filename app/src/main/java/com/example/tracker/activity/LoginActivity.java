package com.example.tracker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tracker.MainActivity;
import com.example.tracker.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout text_inputNum;
    Button button_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text_inputNum = findViewById(R.id.text_dateOfBirth);
        button_login = findViewById(R.id.button_login);

        onClickButtonListener();

    }

    private void onClickButtonListener() {
            button_login.setOnClickListener (
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
            );

    }
}
