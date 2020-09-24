package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DecisionActivity extends AppCompatActivity implements View.OnClickListener {

    private String userType;
    private Button signUp, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_activity);

        userType = (String) getIntent().getStringExtra("userType");

        signUp = (Button)findViewById(R.id.signUpBtn);
        login = (Button)findViewById(R.id.loginBtn);

        signUp.setOnClickListener(this);
        login.setOnClickListener(this);


    }

    private void decisionTaken(int decision){

        // After Pressing Login Button (or View) decision Varibale value will be 1 and 2 for Sign Up button

        if(decision == 1){

            Intent intent = new Intent(DecisionActivity.this, SignUpActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
            finish();

        }else if(decision == 2){

            Intent intent = new Intent(DecisionActivity.this, LoginActivity.class);
            intent.putExtra("userType", userType);
            startActivity(intent);
            finish();

        }

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.signUpBtn){
            decisionTaken(1);
        }else if (view.getId() == R.id.loginBtn){
            decisionTaken(2);
        }

    }
}