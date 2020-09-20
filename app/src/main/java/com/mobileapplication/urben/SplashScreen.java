package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    private String firstName, lastName, password, email, gender;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        isLoggedIn();
    }

    private void isLoggedIn(){

        // Checking whether any user data available or not

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        firstName = sharedPreferences.getString("firstName", "");
        lastName = sharedPreferences.getString("lastName", "");
        password = sharedPreferences.getString("password", "");
        email = sharedPreferences.getString("email", "");
        age = sharedPreferences.getInt("age", 0);
        gender = sharedPreferences.getString("gender", "");

        if(firstName.equals("") || lastName.equals("") ||password.equals("") || email.equals("") || (age == 0) || gender.equals("")){
            // Sign up / Login required

            Intent intent = new Intent(SplashScreen.this, DecisionActivity.class);
            startActivity(intent);

        }else{
            // Profile View
        }

    }
}