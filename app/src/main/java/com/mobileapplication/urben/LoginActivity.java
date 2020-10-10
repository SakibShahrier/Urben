package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passField;
    private Button btn;
    private String userType, email, password;
    private FirebaseAuth mAuth;
    private int warnFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        userType = (String) getIntent().getStringExtra("userType");
        warnFlag = 1;

        emailField = (EditText)findViewById(R.id.email);
        passField = (EditText)findViewById(R.id.passWord);
        btn = (Button)findViewById(R.id.login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDriver();
            }
        });
    }

    private void loginDriver() {
        email = emailField.getText().toString().trim();
        password = passField.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            emailField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Fill in the space...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Valid Email only...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            passField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Fill in the space...", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6){
            passField.requestFocus();
            Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Length should be greater than 5...", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            FirebaseDatabase.getInstance().getReference(userType)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                if(userProfile.getEmail().matches(email)) {
                                    warnFlag = 0;
                                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                                                SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("userName", userProfile.getName());
                                                editor.putString("email", userProfile.getEmail());
                                                editor.putString("userType", userType);
                                                editor.commit();
                                                if(userType.matches("passenger")) {
                                                    Intent intent = new Intent(com.mobileapplication.urben.LoginActivity.this, MapsActivity.class);
                                                    intent.putExtra("userType", userType);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else if(userType.matches("driver")) {
                                                    Intent intent = new Intent(com.mobileapplication.urben.LoginActivity.this, ContractorFirstPage.class);
                                                    intent.putExtra("userType", userType);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else {
                                                    Intent intent = new Intent(com.mobileapplication.urben.LoginActivity.this, AgentHomePage.class);
                                                    intent.putExtra("userType", userType);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            else{
                                                Toast.makeText(com.mobileapplication.urben.LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                            if(warnFlag == 1) {
                                Toast.makeText(com.mobileapplication.urben.LoginActivity.this, "Email ID not found...", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}