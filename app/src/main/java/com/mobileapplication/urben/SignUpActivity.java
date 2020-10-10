package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    // Sign Up info we got from user

    private String userName, password, email, gender, userType;
    private int age = 0;
    private Button btn;
    private EditText nameField, emailField, passwordField, ageField;
    Spinner spinnerGender;
    private String[] genderList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mAuth = FirebaseAuth.getInstance();
        userType = (String) getIntent().getStringExtra("userType");

        nameField = (EditText) findViewById(R.id.nameField);
        passwordField = (EditText)findViewById(R.id.passwordField);
        emailField = (EditText)findViewById(R.id.emailAddress);
        ageField = (EditText)findViewById(R.id.ageField);
        btn = (Button)findViewById(R.id.signUp);
        spinnerGender = (Spinner)findViewById(R.id.gender_spinner);

        genderList = getResources().getStringArray(R.array.gender_list);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter
                <String>(this, R.layout.spinner_extra, R.id.spinner_textID, genderList);
        spinnerGender.setAdapter(arrayAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });

    }

    private void registration() {
        userName = nameField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        gender = spinnerGender.getSelectedItem().toString();

        if(TextUtils.isEmpty(userName)){
            nameField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Fill in the space...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            emailField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Fill in the space..", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Valid Email only...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            passwordField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Fill in the space...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ageField.getText().toString().isEmpty()){
            ageField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Enter age...", Toast.LENGTH_SHORT).show();
            age = 0;
            return;
        }
        else{
            age = Integer.parseInt(ageField.getText().toString());
        }

        if(password.length() < 6){
            passwordField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, "Length should be greater than 5...", Toast.LENGTH_SHORT).show();
            return;
        }

        else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        UserProfile userProfile = new UserProfile(userName, email, age, gender);
                        FirebaseDatabase.getInstance().getReference(userType)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Registration Successful...", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        // FirebaseAuth.getInstance().signOut();
                        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userProfile.getName());
                        editor.putString("email", userProfile.getEmail());
                        editor.putString("userType", userType);
                        editor.commit();
                        if(userType.matches("passenger")) {
                            Intent intent = new Intent(com.mobileapplication.urben.SignUpActivity.this, MapsActivity.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        }
                        else if(userType.matches("driver")) {
                            Intent intent = new Intent(com.mobileapplication.urben.SignUpActivity.this, ContractorFirstPage.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(com.mobileapplication.urben.SignUpActivity.this, AgentHomePage.class);
                            intent.putExtra("userType", userType);
                            startActivity(intent);
                            finish();
                        }
                    }

                    else{
                        Toast.makeText(com.mobileapplication.urben.SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}