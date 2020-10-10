package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;


public class ContractorFirstPage extends AppCompatActivity implements View.OnClickListener {

    private Button rtOne, rtTwo;
    private ImageButton imageButton;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    DriverProfile driverProfile;
    private String userName, email, serviceType;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private double latitude, longitude;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contractor_first_page_activity);
        init();
        locationRunnable.run();

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "");
        email = sharedPreferences.getString("email", "");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.settingsDriver: {
                Toast.makeText(ContractorFirstPage.this, "Hello", Toast.LENGTH_SHORT).show();

                break;
            }

            case R.id.routeOneBtn: {

                int id = radioGroup.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(ContractorFirstPage.this, "Please Select Service Type", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    radioButton = findViewById(id);
                    serviceType = radioButton.getText().toString().trim();
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("busInfo");
                driverProfile = new DriverProfile(userName, email, serviceType, "route_1");
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                databaseReference.child(userID).setValue(driverProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

                Intent intent = new Intent(ContractorFirstPage.this, DropOffActivity.class);
                intent.putExtra("driverProf", (Serializable) driverProfile);
                startActivity(intent);
                finish();

                break;
            }

            case R.id.routeTwoBtn: {

                int id = radioGroup.getCheckedRadioButtonId();
                if (id == -1) {
                    Toast.makeText(ContractorFirstPage.this, "Please Select Service Type", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    radioButton = findViewById(id);
                    serviceType = radioButton.getText().toString().trim();
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("busInfo");
                driverProfile = new DriverProfile(userName, email, serviceType, "route_2");
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                databaseReference.child(userID).setValue(driverProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

                Intent intent = new Intent(ContractorFirstPage.this, DropOffActivity.class);
                intent.putExtra("driverProf", (Serializable) driverProfile);
                startActivity(intent);
                finish();

                break;
            }
        }
    }

    private void init() {

        imageButton = findViewById(R.id.settingsDriver);
        rtOne = findViewById(R.id.routeOneBtn);
        rtTwo = findViewById(R.id.routeTwoBtn);
        radioGroup = findViewById(R.id.serviceGrp);

        imageButton.setOnClickListener(this);
        rtOne.setOnClickListener(this);
        rtTwo.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(ContractorFirstPage.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(ContractorFirstPage.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null  && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        }
                    }
                }, Looper.myLooper());
    }

    private Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ContractorFirstPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                getCurrentLocation();
                // Toast.makeText(ContractorFirstPage.this, "" + latitude, Toast.LENGTH_LONG).show();

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference availability = FirebaseDatabase.getInstance().getReference().child("driver location");
                GeoFire geoFire = new GeoFire(availability);
                if(latitude != 0 && longitude != 0) {
                    geoFire.setLocation(userID, new GeoLocation(latitude, longitude));
                }
            }
            handler.postDelayed(this, 5000);
        }
    };
}