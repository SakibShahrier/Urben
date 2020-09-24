package com.mobileapplication.urben;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserType extends AppCompatActivity implements View.OnClickListener {

    private CardView passenger, driver, agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_type_activity);

        passenger = (CardView)findViewById(R.id.passengerBtn);
        driver = (CardView)findViewById(R.id.driverBtn);
        agent = (CardView)findViewById(R.id.ticketAgent);

        passenger.setOnClickListener(this);
        agent.setOnClickListener(this);
        driver.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.passengerBtn:{
                Intent intent = new Intent(UserType.this, DecisionActivity.class);
                intent.putExtra("userType", "passenger");
                startActivity(intent);
                finish();
                break;
            }
            case R.id.driverBtn:{
                Intent intent = new Intent(UserType.this, DecisionActivity.class);
                intent.putExtra("userType", "driver");
                startActivity(intent);
                finish();
                break;
            }
            case R.id.ticketAgent:{
                Intent intent = new Intent(UserType.this, DecisionActivity.class);
                intent.putExtra("userType", "agent");
                startActivity(intent);
                finish();
                break;
            }
            default:
                break;
        }

    }
}