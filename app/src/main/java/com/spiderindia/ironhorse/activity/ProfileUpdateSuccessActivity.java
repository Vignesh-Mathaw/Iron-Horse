package com.spiderindia.ironhorse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spiderindia.ironhorse.R;

public class ProfileUpdateSuccessActivity extends AppCompatActivity {


    Button btnContinue;
    TextView textview;

    String UserName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update_success);

        textview= (TextView) findViewById(R.id.profileNameinprofilesucess);
        btnContinue = (Button) findViewById(R.id.btncontinueinprofilesucess);

        UserName = getIntent().getStringExtra("USERNAMEFORPROFILE").toString().trim();

        textview.setText("Hi, "+UserName);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ProfileUpdateSuccessActivity.this, MainActivity.class));


            }
        });



    }
}