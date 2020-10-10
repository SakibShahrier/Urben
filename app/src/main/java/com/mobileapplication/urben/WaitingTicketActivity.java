package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class WaitingTicketActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton imageButton;
    TextView textView;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_ticket_activity);

        trip = (Trip) getIntent().getSerializableExtra("tripDetails");
        Log.d("Trip Res" ,trip.toString());

        imageButton = findViewById(R.id.mapViewID);
        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mapViewID){
            Intent intent = new Intent(WaitingTicketActivity.this, WaitingActivity.class);
            intent.putExtra("tripDetails",  trip);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
}