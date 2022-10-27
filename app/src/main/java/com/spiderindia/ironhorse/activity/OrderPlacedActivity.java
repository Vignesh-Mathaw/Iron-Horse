package com.spiderindia.ironhorse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.spiderindia.ironhorse.R;

import com.spiderindia.ironhorse.helper.DatabaseHelper;

public class OrderPlacedActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        databaseHelper = new DatabaseHelper(OrderPlacedActivity.this);
        databaseHelper.DeleteAllOrderData();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView spiderlogo = findViewById(R.id.spiderlogo);

        spiderlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent Getintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spiderindia.com/"));
                    startActivity(Getintent);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnshopping) {
            startActivity(new Intent(OrderPlacedActivity.this, MainActivity.class));
            finishAffinity();
        } else if (id == R.id.txtsummary) {
            startActivity(new Intent(OrderPlacedActivity.this, OrderListActivity.class));
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
