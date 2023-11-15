package com.example.brewbot;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.brewbot.httptasks.LoginTask;

import org.dmg.pmml.PMML;
import org.pmml4s.common.Evaluator;
import org.pmml4s.model.Model;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import jakarta.xml.bind.JAXBException;

public class MainActivity extends AppCompatActivity {
    private Button newUserButton;
    private Button loginButton;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText)findViewById(R.id.emailEdit);
        password = (EditText)findViewById(R.id.passEdit);

        loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> login());

        newUserButton = (Button)findViewById(R.id.register);
        newUserButton.setOnClickListener(view -> registerAccount());

    }

    public void goToHomeScreen(){
        Intent intent = new Intent(this, homescreenActivity.class);
        startActivity(intent);
    }


    public void login(){ //TODO DIT WERKT NIET
        System.out.println("loginbutton");
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        LoginTask loginTask = new LoginTask(emailText, passwordText, this);
        loginTask.execute();
    }
    public void registerAccount(){
        // TODO: Send details to database
        Intent intent = new Intent(this, registerActivity.class);
        startActivity(intent);
    }
}