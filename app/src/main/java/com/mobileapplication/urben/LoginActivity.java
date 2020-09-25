package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passField;
    private Button btn;
    private String userType, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        userType = (String) getIntent().getStringExtra("userType");

        emailField = (EditText)findViewById(R.id.email);
        passField = (EditText)findViewById(R.id.passWord);
        btn = (Button)findViewById(R.id.login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gatheringLoginData();

                if(checkingDataAvailability()){
                    // Firebase Login Auth code here ;)
                }

            }
        });

    }

    private void gatheringLoginData(){
        email = emailField.getText().toString().trim();
        password = passField.getText().toString().trim();
    }


    private boolean checkingDataAvailability(){
        if(new RequiredDataChecker().providedLoginDataProperly(email, password)){
            return true;
        }else{
            Toast.makeText(LoginActivity.this, "Email or Password Required", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}