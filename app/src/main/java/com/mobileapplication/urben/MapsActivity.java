package com.mobileapplication.urben;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

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
    private LocationCallback mLocationCallback;
    private boolean locationGranted = false;
    private static final int locationPermissionRequestCode = 1234;
    private ArrayList<String> places = new ArrayList<>();
    private ArrayList<String> coordinates = new ArrayList<>();

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
                Toast.makeText(MapsActivity.this, editText.getText().toString().trim(), Toast.LENGTH_SHORT).show();
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
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
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
        Toast.makeText(MapsActivity.this, lastLocation.getLatitude() + " " + lastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        Log.d("MapsActivity", lastLocation.getLatitude() + " " + lastLocation.getLongitude());
    }

    private void initializeMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "initializeMap() Called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }
}