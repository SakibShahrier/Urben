package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WaitingActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private ImageButton imageButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    private float defaultZoom = 12.5f;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private ArrayList<String> places = new ArrayList<>();
    private ArrayList<String> coordinates = new ArrayList<>();
    private ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    Trip trip;
    private int INSERT_LOC = 1;
    private Handler handler = new Handler();
    DriverProfile[] driverProfile = new DriverProfile[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);

        trip = (Trip) getIntent().getSerializableExtra("tripDetails");
        initMap();
        imageButton = (ImageButton) findViewById(R.id.ticketViewID);

        imageButton.setOnClickListener(this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("locations");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                places.clear();
                coordinates.clear();
                int i = 0;
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    places.add(dataSnapshot.getValue(LocationProfile.class).getPlaces());
                    coordinates.add(dataSnapshot.getValue(LocationProfile.class).getCoordinates());
                    i++;
                }
                latLongSplitter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        locationRunnable.run();
    }

    private void latLongSplitter() {
        String[] temp;
        double y, z;

        for (String x : coordinates) {
            temp = x.split(",");
            y = Double.parseDouble(temp[0]);
            z = Double.parseDouble(temp[1]);
            LatLng latLng = new LatLng(y, z);
            arrayList.add(latLng);
        }
    }

    private void placeTicketer() {
        LatLng customerPickUpLocation = arrayList.get(places.indexOf(trip.getStartLocation()));
        mMap.addMarker(new MarkerOptions().position(customerPickUpLocation).title(trip.getStartLocation()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPickUpLocation, 14.5f));
    }

    private void initMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();

        mapSetting();
    }

    private void mapSetting() {
        getDeviceLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void getDeviceLocation() {
        Log.d("MapsActivity", "getDeviceLocation() Called");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task location = fusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Log.d("MapsActivity", "Found");
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), defaultZoom);
                    // Toast.makeText(getApplicationContext(), "Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.ticketViewID){
            Intent intent = new Intent(WaitingActivity.this, WaitingTicketActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(INSERT_LOC == 1) {
            placeTicketer();
            INSERT_LOC = 0;
        }
        getDeviceLocation();
    }

    private Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("busInfo");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        driverProfile[i] = dataSnapshot.getValue(DriverProfile.class);

                        if(trip.getServiceType().matches("AC") && driverProfile[i].getServiceType().matches("AC")) {
                            DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driver location");
                            GeoFire geoFire = new GeoFire(driverLocation);
                            String userID = dataSnapshot.getKey();
                            geoFire.getLocation(userID, new LocationCallback() {
                                @Override
                                public void onLocationResult(String key, GeoLocation location) {
                                    if (location != null) {
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                                                .title(trip.getStartLocation())).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else if (trip.getServiceType().matches("NAC") && driverProfile[i].getServiceType().matches("NAC")) {
                            DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driver location");
                            GeoFire geoFire = new GeoFire(driverLocation);
                            String userID = dataSnapshot.getKey();
                            geoFire.getLocation(userID, new LocationCallback() {
                                @Override
                                public void onLocationResult(String key, GeoLocation location) {
                                    if (location != null) {
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                                                .title(trip.getStartLocation())).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            handler.postDelayed(this, 5000);
        }
    };
}