package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;

public class SignUpActivity extends AppCompatActivity {

    // Sign Up info we got from user

    private String userName, password, email, gender, userType;
    private int age = 0;
    private Button btn;
    private EditText nameField, emailField, passwordField, ageField;
    Spinner spinnerGender;
    private String[] genderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        userType = (String) getIntent().getStringExtra("userType");

        nameField = (EditText) findViewById(R.id.nameField);
        passwordField = (EditText)findViewById(R.id.passwordField);
        emailField = (EditText)findViewById(R.id.emailAddress);
        ageField = (EditText)findViewById(R.id.ageField);
        btn = (Button)findViewById(R.id.signUp);
        spinnerGender = (Spinner)findViewById(R.id.gender_spinner);

        genderList = getResources().getStringArray(R.array.gender_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_extra, R.id.spinner_textID, genderList);
        spinnerGender.setAdapter(arrayAdapter);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                makeUserDataReady(); // Fetching EditText Data
                if(checkingDataAvailability()){   // Checking if all required data given
                    // firbase Auth code here
                }


            }
        });


    }

    private void makeUserDataReady(){

        userName = nameField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        age = Integer.parseInt(ageField.getText().toString().trim());
        gender = spinnerGender.getSelectedItem().toString();

    }

    private boolean checkingDataAvailability(){
        if(new RequiredDataChecker().providedSignUpDataProperly(userName, email, age, gender, password)){
            UserProfile userProfile = new UserProfile(userName, email, age, gender, password, userType);
            return true;
        }else{
            Toast.makeText(SignUpActivity.this, "Please Give All the Information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }




}