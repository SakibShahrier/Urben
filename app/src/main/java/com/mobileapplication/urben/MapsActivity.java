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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import com.google.maps.GeoApiContext;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private String userLocation, destination;
    private LatLng userLatLong, desLatLong;
    private GeoApiContext geoApiContext = null;
    private int INSERT_LOC = 1;

    private static final String TAG = "MapsActivity";
    private ImageButton imgBtn;
    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private float defaultZoom = 12f;
    private AutoCompleteTextView editText;
    private ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    private boolean locationGranted = false;
    private static final int locationPermissionRequestCode = 1234;
    private ArrayList<String> places = new ArrayList<>();
    private ArrayList<String> coordinates = new ArrayList<>();
    private String userType;
    private String name, email;
    private ImageButton profile;

    private  void getLocationPermission(){
        Log.d(TAG, "getLocationPermission() Called");

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationGranted = true;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, locationPermissionRequestCode);
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermissionRequestCode);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationGranted = false;
        Log.d(TAG, "onRequestPermissionsResult() Called");

        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                locationGranted = true;
            }
            else {
                Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show();
                locationGranted = false;
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initializeMap();

        userType = (String) getIntent().getStringExtra("userType");
        profile = (ImageButton)findViewById(R.id.prof);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);

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
                adapter.notifyDataSetChanged();
                editText.setAdapter(adapter);
                latLongSplitter();
                placeTicketer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editText = (AutoCompleteTextView) findViewById(R.id.searchBox);
        imgBtn = (ImageButton)findViewById(R.id.searchBtn);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ProfileMenu.class);
                startActivity(intent);
            }
        });

        getLocationPermission();

        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (checker(editText.getText().toString().trim())){
                        Toast.makeText(MapsActivity.this, editText.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MapsActivity.this, "Nope -_-", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checker(editText.getText().toString().trim())){
                    try{
                        scanCounterCode();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    return;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady() Called");
        mMap = googleMap;
        buildGoogleApiClient();
        Toast.makeText(getApplicationContext(), "Map Ready ;)", Toast.LENGTH_SHORT).show();

        if(locationGranted){
            mapSetting();
            // placePolyLine();
        }
    }

    private void mapSetting() {
        getDeviceLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
                    Toast.makeText(getApplicationContext(), "Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private boolean checker(String s){

        for (String x : places){
            if(x.equals(s)){
                return true;
            }
        }
        return false;
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
        int i = 0;
        for (LatLng l : arrayList) {
            mMap.addMarker(new MarkerOptions().position(l).title(places.get(i)));
            i++;
        }

        // mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(2)));
        // mMap.moveCamera(CameraUpdateFactory.zoomTo(defaultZoom));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(9), 13));
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
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
        Log.d(TAG, "getLocationUpdates() Called");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        // Toast.makeText(MapsActivity.this, lastLocation.getLatitude() + " " + lastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        // Log.d("MapsActivity", lastLocation.getLatitude() + " " + lastLocation.getLongitude());

        if(INSERT_LOC == 1){
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference availability = FirebaseDatabase.getInstance().getReference().child("passenger location");
            GeoFire geoFire = new GeoFire(availability);
            geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
        }
    }

    private void initializeMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "initializeMap() Called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        // Direction Api
        if (geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
    }

    private void scanCounterCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(QRCodeCapture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning Your Location");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String[] codeResult;
        ArrayList<Integer> locationResult;
        double ACFare = 0.00;
        double NONACFare = 0.00;
        String routeName = "";
        int i = 0;

        if (result != null) {
            if (result.getContents() != null) {
                codeResult = result.getContents().split(",");
                destination = editText.getText().toString().trim();
                userLocation = codeResult[0];
                i = new LocationHandler().locationPosition(destination, coordinates);
                desLatLong = new LocationHandler().getLatLongByPosition(i, coordinates);
                userLatLong = new LatLng(Double.parseDouble(codeResult[1]), Double.parseDouble(codeResult[2]));
                locationResult = new LocationHandler().distanceBetween(userLocation, destination, places);
                ACFare = new Trip().calFare(locationResult.get(2), "AC");
                NONACFare = new Trip().calFare(locationResult.get(2), "NON_AC");
                routeName = new LocationHandler().routeName(locationResult.get(0), locationResult.get(1));
                Log.d("Locations", "userLocation: " + userLocation + " Destination : " + destination);
                Log.d("Locations", "Route: " + routeName);

                Intent intent = new Intent(MapsActivity.this, TransportTypeActivity.class);
                intent.putExtra("ACFare", ACFare);
                intent.putExtra("NONACFare", NONACFare);
                intent.putExtra("startLocation", userLocation);
                intent.putExtra("endLocation", destination);
                intent.putExtra("routeName", routeName);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void placePolyLine(){
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(24.896158, 91.861325), new LatLng(24.905977, 91.865241))
                .width(5)
                .color(Color.BLUE));
    }

    private void afterPayment() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference availability = FirebaseDatabase.getInstance().getReference().child("passenger location");
        GeoFire geoFire = new GeoFire(availability);
        geoFire.setLocation(userID, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));
        LatLng customerPickUpLocation = arrayList.get(places.indexOf(editText.getText().toString().trim()));
        mMap.addMarker(new MarkerOptions().position(customerPickUpLocation).title(editText.getText().toString().trim()));
        mMap.addMarker(new MarkerOptions().position(customerPickUpLocation).title(editText.getText().toString().trim()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerPickUpLocation, 17));
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*INSERT_LOC = 0;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference availability = FirebaseDatabase.getInstance().getReference().child("passenger location");
        GeoFire geoFire = new GeoFire(availability);
        geoFire.removeLocation(userID);
        finish();*/
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference(userType)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                            String authEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if(userProfile.getEmail().matches(authEmail)) {
                                email = userProfile.getEmail();
                                name = userProfile.getName();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            userInfo();
        }
        catch (Exception e) {

        }
    }
}