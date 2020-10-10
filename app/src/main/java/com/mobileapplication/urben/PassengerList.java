package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.Serializable;
import java.util.ArrayList;

public class PassengerList extends AppCompatActivity {

    private ListView listView;
    private Button button;
    private AlertDialog.Builder alertDialog;
    private String currentLocation;
    private DriverProfile driverProfile;
    private String[] listOfToken = new String[100];
    int totalPassengerToDrop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passenger_list_activity);

        FirebaseDatabase.getInstance().getReference("trip")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            if(driverProfile.getServiceType().matches(trip.getServiceType())  && trip.getEndLocation().matches(currentLocation)) {
                                listOfToken[i] = trip.getTicketID() + " " + trip.getPassengerName();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        currentLocation = getIntent().getStringExtra("stoppage");
        driverProfile = (DriverProfile) getIntent().getSerializableExtra("driverProf"); // driverProfile.getServiceType

        listView = findViewById(R.id.passengerList);
        button = findViewById(R.id.droppedBtn);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PassengerList.this, R.layout.passenger_list_activity, R.id.passengerListView, listOfToken);
        listView.setAdapter(arrayAdapter);
        totalPassengerToDrop = listOfToken.length;

        TextView textView = findViewById(R.id.passengerCount);
        textView.setText("" + totalPassengerToDrop);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Taking Permission
                opeDialog();
                FirebaseDatabase.getInstance().getReference("trip")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int i = 0;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Trip trip = dataSnapshot.getValue(Trip.class);
                                    String[] temp = listOfToken[i].split(" ");
                                    if(trip.getPassengerName().matches(temp[0]) && trip.getTicketID().matches(temp[1])) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("trip");
                                        String userID = snapshot.getKey();
                                        databaseReference.child(userID).removeValue();

                                        i++;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
    }

    private void opeDialog(){
        alertDialog = new AlertDialog.Builder(PassengerList.this);
        alertDialog.setTitle("Attention!");
        alertDialog.setMessage("Want to Receive more Passenger ?");
        alertDialog.setIcon(R.drawable.attention_icon);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PassengerList.this, DropOffActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }


}