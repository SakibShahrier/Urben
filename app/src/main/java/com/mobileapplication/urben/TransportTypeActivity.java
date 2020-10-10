package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class TransportTypeActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] busTypes;
    private Button ac, nonac;
    private double acFare = 0.00, nonacFare = 0.00;
    private String startLocation = "", endLocation = "", routeName = "";
    private String email, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_type_activity);

        busTypes = getResources().getStringArray(R.array.vehicle_type);
        ac = (Button)findViewById(R.id.acBtn);
        nonac = (Button)findViewById(R.id.nonacBtn);

        acFare = (double)getIntent().getDoubleExtra("ACFare", 0.00);
        nonacFare = (double)getIntent().getDoubleExtra("NONACFare", 0.00);
        startLocation = (String)getIntent().getStringExtra("startLocation");
        endLocation = (String)getIntent().getStringExtra("endLocation");
        routeName = (String)getIntent().getStringExtra("routeName");

        email = (String)getIntent().getStringExtra("email");
        name = (String)getIntent().getStringExtra("name");

        ac.setText("AC Fare :   " + acFare + " BDT");
        nonac.setText("Non-AC Fare :   " + nonacFare + " BDT");


        ac.setOnClickListener(this);
        nonac.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.acBtn){

            Intent intent = new Intent(TransportTypeActivity.this, BillingActivity.class);
            intent.putExtra("fare", acFare);
            intent.putExtra("startLocation", startLocation);
            intent.putExtra("endLocation", endLocation);
            intent.putExtra("routeName", routeName);
            intent.putExtra("email", email);
            intent.putExtra("name", name);
            intent.putExtra("serviceType", "AC");
            Log.d("Loc-TransportType", "Start : " + startLocation + "End : " + endLocation);
            startActivity(intent);
            finish();

        }else if(view.getId() == R.id.nonacBtn){
            Intent intent = new Intent(TransportTypeActivity.this, BillingActivity.class);
            intent.putExtra("fare", nonacFare);
            intent.putExtra("startLocation", startLocation);
            intent.putExtra("endLocation", endLocation);
            intent.putExtra("routeName", routeName);
            intent.putExtra("email", email);
            intent.putExtra("name", name);
            intent.putExtra("serviceType", "NAC");
            Log.d("Loc-TransportType", "Start : " + startLocation + "End : " + endLocation + "route : "  + routeName);
            startActivity(intent);
            finish();

        }
    }
}