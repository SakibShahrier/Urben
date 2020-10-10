package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class BillingActivity extends AppCompatActivity {

    private Button btn, ticketBtn;
    private TextView fareText, start, end, arrival;
    private String startLocation = "", endLocation = "", routeName = "";
    private double fare = 0.00;
    private boolean ticketBtnAvailable = false, paymentBtn = true;
    private String email, name, serviceType;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_activity);

        fare = (double)getIntent().getDoubleExtra("fare", 0.00);
        startLocation = (String)getIntent().getStringExtra("startLocation");
        endLocation = (String)getIntent().getStringExtra("endLocation");
        routeName = (String)getIntent().getStringExtra("routeName");
        email = (String)getIntent().getStringExtra("email");
        name = (String)getIntent().getStringExtra("name");
        serviceType = (String)getIntent().getStringExtra("serviceType");


        Log.d("Loc-TransportType", "Start : " + startLocation + "End : " + endLocation);

        btn = (Button)findViewById(R.id.paymentBtn);
        ticketBtn = (Button)findViewById(R.id.ticketID) ;
        fareText = (TextView) findViewById(R.id.fareText);
        start = (TextView)findViewById(R.id.startLocationText);
        end = (TextView)findViewById(R.id.endLocationText);
        arrival = (TextView)findViewById(R.id.arrivalTime);
        ticketBtn.setVisibility(View.GONE);

        fareText.setText("" + fare + " BDT");
        start.setText(startLocation);
        end.setText(endLocation);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentBtn){
                    fareText.setText("PAID");
                    fareText.setTextColor(Color.rgb(0, 187, 78));
                    ticketBtnAvailable = true;
                    btn.setVisibility(View.GONE);
                    paymentBtn = false;
                    ticketBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        ticketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ticketBtnAvailable){
                    trip = new Trip(startLocation, endLocation, fare, routeName, email, name, serviceType);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trip");
                    String key = databaseReference.push().getKey();
                    databaseReference.child(key).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(BillingActivity.this, WaitingTicketActivity.class);
                            intent.putExtra("tripDetails",  trip);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }
        });

    }
}