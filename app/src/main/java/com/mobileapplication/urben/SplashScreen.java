package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;

public class SplashScreen extends AppCompatActivity {

    private String userName, password, email, gender, userType;
    private int age = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_screen);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(3000);
                    isLoggedIn();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void isLoggedIn(){

        // Checking whether any user data available or not

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "");
        password = sharedPreferences.getString("password", "");
        email = sharedPreferences.getString("email", "");
        age = sharedPreferences.getInt("age", 0);
        gender = sharedPreferences.getString("gender", "");
        userType = sharedPreferences.getString("userType", "");

        if(!(new RequiredDataChecker().sharedDataAvailable(userName, email, age, gender, password, userType))){
            // Sign up / Login required

            Intent intent = new Intent(SplashScreen.this, UserType.class);
            startActivity(intent);
            finish();

        }else{
            // Profile View

            UserProfile userProfile = new UserProfile(userName, email, age, gender);
            Intent intent = new Intent(SplashScreen.this, ProfileActivity.class);
            intent.putExtra("userDetails", (Serializable) userProfile);
            startActivity(intent);
        }

    }
}