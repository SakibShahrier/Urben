package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private String userName, email, userType;

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
        email = sharedPreferences.getString("email", "");
        userType = sharedPreferences.getString("userType", "");

        if(!(new RequiredDataChecker().sharedDataAvailable(userName, email, userType))){
            // Sign up / Login required

            Intent intent = new Intent(SplashScreen.this, UserType.class);
            startActivity(intent);
            finish();

        }
        else{
            // Maps Activity
            if(userType.matches("passenger")) {
                Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish();
            }
            else if(userType.matches("driver")) {
                Intent intent = new Intent(SplashScreen.this, ContractorFirstPage.class);
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashScreen.this, AgentHomePage.class);
                intent.putExtra("userType", userType);
                startActivity(intent);
                finish();
            }
        }
    }

}