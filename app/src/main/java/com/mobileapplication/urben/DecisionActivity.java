package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DecisionActivity extends AppCompatActivity {

    private int decision = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decision_activity);
    }

    private void decisionTaken(){

        // After Pressing Login Button (or View) decision Varibale value will be 1 and 2 for Sign Up button

        if(decision == 1){

            Intent intent = new Intent(DecisionActivity.this, LoginActivity.class);
            startActivity(intent);

        }else if(decision == 2){

            Intent intent = new Intent(DecisionActivity.this, SignUpActivity.class);
            startActivity(intent);

        }

    }
}