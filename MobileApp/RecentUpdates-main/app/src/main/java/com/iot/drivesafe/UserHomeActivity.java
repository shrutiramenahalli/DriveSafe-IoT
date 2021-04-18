package com.iot.drivesafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iot.drivesafe.User;

public class UserHomeActivity extends AppCompatActivity {

    String api_key="rR8dylZSrx7TYlzyqvfhK8y2Ds7lYBoY6LzAiGMa";

    public CardView card1, card2, card3, card4, card5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        //Intent intent = getIntent();
        //String st =intent.getStringExtra("passed_email");

        card1 = (CardView) findViewById(R.id.Card1);
        card2 = (CardView) findViewById(R.id.Card2);
        card3 = (CardView) findViewById(R.id.Card3);
        card4 = (CardView) findViewById(R.id.Card4);
        card5 = (CardView) findViewById(R.id.Card5);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                //i1.putExtra("passed_email2", st);
                startActivity(i1);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(UserHomeActivity.this, PaymentsActivity.class);
                //i2.putExtra("passed_email2", st);
                startActivity(i2);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = getIntent();
//                String st =intent.getStringExtra("passed_email");
                Intent i3 = new Intent(UserHomeActivity.this, Balance.class);
                //i3.putExtra("passed_email", st);
                startActivity(i3);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i4 = new Intent(UserHomeActivity.this, Alcohol.class);
                //i4.putExtra("passed_email", st);
                startActivity(i4);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = getIntent();
//                String st =intent.getStringExtra("passed_email");
                Intent i5 = new Intent(UserHomeActivity.this, LoginActivity.class);
                startActivity(i5);
            }
        });
    }}