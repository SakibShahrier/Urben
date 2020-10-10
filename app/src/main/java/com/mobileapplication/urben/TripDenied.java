package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TripDenied extends AppCompatActivity {

    Button btn;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_denied_activity);

        btn = findViewById(R.id.deniedSubmit);
        editText = findViewById(R.id.reasonField);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Some Action

                if(!editText.getText().toString().equals("")){
                    Intent intent = new Intent(TripDenied.this, AgentHomePage.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(TripDenied.this, "Please Enter a Valid Reason", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(TripDenied.this, "Please Enter Reason First", Toast.LENGTH_SHORT).show();
    }
}