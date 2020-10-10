package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DropOffActivity extends AppCompatActivity {

    private TextView locOne, locTwo, locThree;
    private TextView cOne, cTwo, cThree;
    private double currentLat, currentLong;
    private String route;
    private String[] coordinates_two, coordinates_one;
    private String[] abc;
    private ArrayList<String> coordinates = new ArrayList<>(), locations = new ArrayList<>();
    private static int position = 0;
    private  double nextLat, nextLong;
    private double distance = 0;
    private int locationOneCount = 0, locationTwoCount = 0, locationThreeCount = 0;
    private Handler handler = new Handler();
    private DriverProfile driverProfile;
    private static final double MIN_DISTANCE = 5.00;
    private  static boolean RECEIVE_PASSENGER = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_off_activity);
        init();

        tripCount.run();
    }

    private void init(){
        locOne = findViewById(R.id.locationOne);
        locTwo = findViewById(R.id.locationTwo);
        locThree = findViewById(R.id.locationThree);

        cOne = findViewById(R.id.countOne);
        cTwo = findViewById(R.id.countTwo);
        cThree = findViewById(R.id.countThree);

        driverProfile = (DriverProfile) getIntent().getSerializableExtra("driverProf");
        route = driverProfile.getRouteName();


        // DB Coordinates
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("locations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locations.clear();
                coordinates.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    locations.add(dataSnapshot.getValue(LocationProfile.class).getPlaces());
                    coordinates.add(dataSnapshot.getValue(LocationProfile.class).getCoordinates());
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createDropOffList(){
        if(route.equals("route_1")){
            coordinates_one = arrayListToArray(coordinates, coordinates_one); //
            String[] temp = coordinates_one[position].split(",");
            currentLat = Double.parseDouble(temp[0]); // Driver actual Lat
            currentLong = Double.parseDouble(temp[1]); // Driver actual long
            temp = coordinates_one[position + 1].split(",");
            nextLat = Double.parseDouble(temp[0]);
            nextLong = Double.parseDouble(temp[1]);
            abc = arrayListToArray(locations, abc);

            locOne.setText(abc[position + 1]);
            locTwo.setText(abc[position + 2]);
            locThree.setText(abc[position + 3]);;
            if(currentLat < nextLat){

            }else if(currentLat >= nextLong){
                position++;
            }

            distance = new LocationHandler().calDistance(new LatLng(currentLat, currentLong), new LatLng(nextLat, nextLong));
            if(distance <= MIN_DISTANCE){
                // Go to PassengerList
                handler.removeCallbacks(tripCount);

                FirebaseDatabase.getInstance().getReference("agentInfo")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    AgentProfile agentProfile = dataSnapshot.getValue(AgentProfile.class);
                                    if(agentProfile.getCounterName().matches(abc[position + 1])) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("agentInfo");
                                        String userID = snapshot.getKey();
                                        databaseReference.child(userID).child("busArrived").setValue(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                Intent intent = new Intent(DropOffActivity.this, PassengerList.class);
                intent.putExtra("driverProf", (Serializable) driverProfile);
                intent.putExtra("stoppage", abc[position + 1]);
                startActivity(intent);
            }
        }
        else if(route.equals("route_2")){
            coordinates_two = reverseStringArray(coordinates, coordinates_two); //
            String[] temp = coordinates_two[position].split(",");
            currentLat = Double.parseDouble(temp[0]); // Driver actual Lat
            currentLong = Double.parseDouble(temp[1]); // Driver actual long
            temp = coordinates_two[position + 1].split(",");
            nextLat = Double.parseDouble(temp[0]);
            nextLong = Double.parseDouble(temp[1]);
            abc = reverseStringArray(locations, abc);

            locOne.setText(abc[position + 1]);
            locTwo.setText(abc[position + 2]);
            locThree.setText(abc[position + 3]);;
            if(currentLat < nextLat){

            }else if(currentLat >= nextLong){
                position++;
            }

            distance = new LocationHandler().calDistance(new LatLng(currentLat, currentLong), new LatLng(nextLat, nextLong));
            if(distance <= MIN_DISTANCE){
                // Go to PassengerList
                handler.removeCallbacks(tripCount);

                FirebaseDatabase.getInstance().getReference("agentInfo")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                    AgentProfile agentProfile = dataSnapshot.getValue(AgentProfile.class);
                                    if(agentProfile.getCounterName().matches(abc[position + 1])) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("agentInfo");
                                        String userID = snapshot.getKey();
                                        databaseReference.child(userID).child("busArrived").setValue(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                Intent intent = new Intent(DropOffActivity.this, PassengerList.class);
                intent.putExtra("driverProf", (Serializable) driverProfile);
                intent.putExtra("stoppage", abc[position + 1]);
                startActivity(intent);
            }

        }
    }

    private String[] reverseStringArray(ArrayList<String> arr, String[] arr1){
        Collections.reverse(arr);
        arr1 = arr.toArray(arr1);
        return arr1;
    }

    private String[] arrayListToArray(ArrayList<String> arr, String[] arr1){
        arr1 = arr.toArray(arr1);
        return arr1;
    }

    private Runnable tripCount = new Runnable() {
        @Override
        public void run() {
            createDropOffList();
            FirebaseDatabase.getInstance().getReference("trip")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int i = 0;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip trip = dataSnapshot.getValue(Trip.class);
                                if(trip.getServiceType().matches(driverProfile.getServiceType()) && trip.getStartLocation().matches(abc[position + 1])) {
                                    i++;
                                }
                            }
                            locationOneCount = i;

                            i = 0;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip trip = dataSnapshot.getValue(Trip.class);
                                if(trip.getServiceType().matches(driverProfile.getServiceType()) && trip.getStartLocation().matches(abc[position + 2])) {
                                    i++;
                                }
                            }

                            locationTwoCount = i;

                            i = 0;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Trip trip = dataSnapshot.getValue(Trip.class);
                                if(trip.getServiceType().matches(driverProfile.getServiceType()) && trip.getStartLocation().matches(abc[position + 3])) {
                                    i++;
                                }
                            }

                            locationThreeCount = i;

                            cOne.setText("" + locationOneCount);
                            cTwo.setText("" + locationTwoCount);
                            cThree.setText("" + locationThreeCount);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            handler.postDelayed(this, 15000);
        }
    };
}