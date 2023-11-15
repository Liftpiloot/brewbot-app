package com.example.brewbot;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.CaseMap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.brewbot.httptasks.RegisterUser;

public class registerActivity extends AppCompatActivity {
    private EditText age;
    private EditText username;
    private EditText email;
    private EditText password;
    private RadioGroup gender;
    private Button registerButton;
    private String genderValue;
    private int ageValue;
    private String usernameValue;
    private String emailValue;
    private String passwordValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Bind ID's to variable names
        age = findViewById(R.id.editTextAge);
        username = findViewById(R.id.editTextUserName);
        email = findViewById(R.id.editTextMail);
        password = findViewById(R.id.editTextPassword);
        gender = findViewById(R.id.radioGroup);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int selectedID = gender.getCheckedRadioButtonId();
                    RadioButton selectedButton = findViewById(selectedID);
                    genderValue = selectedButton.getText().toString().toLowerCase();
                    emailValue = email.getText().toString();
                    usernameValue = username.getText().toString();
                    passwordValue = password.getText().toString();
                    ageValue = Integer.parseInt(age.getText().toString());
                }
                catch (Exception e){
                    showError("Fill in all required fields");
                }
                registerUser();
            }
        });
    }
    public void registerUser(){
        System.out.println("REGISTER BUTTON: " + emailValue);
        RegisterUser register = new RegisterUser(emailValue, usernameValue, passwordValue, ageValue, genderValue, this);
        register.execute();
    }
    protected void showError(String message)
    {
        Handler handler =  new Handler(this.getMainLooper());
        handler.post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

}