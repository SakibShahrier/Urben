package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgentHomePage extends AppCompatActivity {


    private ImageButton imageButton;
    private TextView acPassengerCount, nonacPassengerCount, counter, counterText, acText, nonacText;
    private AlertDialog.Builder alertDialog;
    private String counterName, lat, lon;
    private double agentLat, agentLong;
    private boolean busArrived = false;
    private String userName, email;
    private Handler handler = new Handler();
    private int ac = 0, nac = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent_home_page_activity);
        boolean dataAvailable = init();

        if(dataAvailable){
            getData();
            SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
            userName = sharedPreferences.getString("userName", "");
            email = sharedPreferences.getString("email", "");
            AgentProfile agentInfo = new AgentProfile(userName, email);
            agentInfo.setCounterName(counterName);
            agentInfo.setCounterLat(agentLat);
            agentInfo.setCounterLong(agentLong);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("agentInfo");
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            databaseReference.child(userID).setValue(agentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });

            tripCount.run();
        }
        else{
            askingForInfo();
        }



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AgentHomePage.this, "Murgi Khaba ? :P", Toast.LENGTH_SHORT).show();
                allowPassenger();
            }
        });

    }

    private void allowPassenger(){
        alertDialog = new AlertDialog.Builder(AgentHomePage.this);
        alertDialog.setTitle("Waiting For Approval");
        alertDialog.setMessage("\tAllow 60 Passenger to \n\tGreen Dhaka Paribahan ?!");
        alertDialog.setIcon(R.drawable.attention_icon);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Positive or Approval Code
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Denied and Reasoning
                Intent intent = new Intent(AgentHomePage.this, TripDenied.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void askingForInfo(){

        disable();
        alertDialog = new AlertDialog.Builder(AgentHomePage.this);
        alertDialog.setTitle("Information");
        alertDialog.setMessage("\tNeed Some Missing Information \n\tProvide those to continue !!!");
        alertDialog.setIcon(R.drawable.attention_icon);
        alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Positive or Approval Code
                Intent intent = new Intent(AgentHomePage.this, AgentEditProfile.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();

    }

    private boolean init(){
        SharedPreferences sharedPreferences = getSharedPreferences("agentDetails", Context.MODE_PRIVATE);
        counterName = sharedPreferences.getString("counterName", "");
        lat = sharedPreferences.getString("email", "");
        lon = sharedPreferences.getString("userType", "");

        imageButton = findViewById(R.id.settingsAgent);
        acPassengerCount = findViewById(R.id.ACcount);
        nonacPassengerCount = findViewById(R.id.nonACcount);
        counter = findViewById(R.id.counterName);
        acText = findViewById(R.id.acText);
        nonacText = findViewById(R.id.nonacText);
        counterText = findViewById(R.id.counterText);

        if (new RequiredDataChecker().agentDataAvailable(counterName, lat, lon)){
            return true;
        }
        return false;
    }

    private void disable(){
        counterText.setVisibility(View.GONE);
        counter.setText("No Info, No Job");
        acText.setVisibility(View.GONE);
        nonacText.setVisibility(View.GONE);
        acPassengerCount.setVisibility(View.GONE);
        nonacPassengerCount.setVisibility(View.GONE);
    }

    private void getData(){

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        counterName = sharedPreferences.getString("counterName", "");
        lat = sharedPreferences.getString("lat", "");
        lon = sharedPreferences.getString("lon", "");

        agentLat = Double.parseDouble(lat);
        agentLong = Double.parseDouble(lon);

        counter.setText(counterName);
        acText.setVisibility(View.VISIBLE);
        nonacText.setVisibility(View.VISIBLE);
        acPassengerCount.setVisibility(View.VISIBLE);
        nonacPassengerCount.setVisibility(View.VISIBLE);
        counterText.setVisibility(View.VISIBLE);
    }

    private Runnable tripCount = new Runnable() {
        @Override
        public void run() {
            FirebaseDatabase.getInstance().getReference("trip")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ac = nac = 0;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip trip = dataSnapshot.getValue(Trip.class);
                                if(trip.getServiceType().matches("AC") && trip.getStartLocation().matches(counterName)) {
                                    ac++;
                                }
                                else if (trip.getServiceType().matches("NAC") && trip.getStartLocation().matches(counterName)) {
                                    nac++;
                                }
                            }
                            acPassengerCount.setText("" + ac);
                            nonacPassengerCount.setText("" + nac);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            handler.postDelayed(this, 15000);
        }
    };
}