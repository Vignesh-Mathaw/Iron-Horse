package com.spiderindia.ironhorse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.adapter.AddressAdapter;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressShowActivity  extends AppCompatActivity implements OnItemClick {

    static Session session;
    Button btnAddAddress;
    ImageView btnback;
    LinearLayout lytNoaddress;
    RelativeLayout lytList;
    RecyclerView addressList;

    Button btnAddNew,btnContinue;

    public static ArrayList<Address> AddressArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showaddress);

        session = new Session(AddressShowActivity.this);

        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnback = findViewById(R.id.btnback);
        lytList= findViewById(R.id.lytList);
        btnAddNew= findViewById(R.id.btnAddNew);
        btnContinue= findViewById(R.id.btnContinue);
        lytNoaddress= findViewById(R.id.lytNoaddress);
        addressList= findViewById(R.id.addressList);
        addressList.setLayoutManager(new LinearLayoutManager(this));
        addressList.setNestedScrollingEnabled(false);
        addressList.setItemViewCacheSize(2);

        lytNoaddress.setVisibility(View.VISIBLE);
        lytList.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressShowActivity.this, CheckoutActivity.class));
            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressShowActivity.this, AddressAddActivity.class));
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressShowActivity.this, AddressAddActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressData();
    }

    public void getAddressData(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("type","list_address");
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray jsonArray3 = json.getJSONArray("data");
                        AddressArrayList = new ArrayList<>();
                        AddressArrayList.clear();
                        for (int i = 0; i < jsonArray3.length(); i++) {
                            JSONObject jsonObject = jsonArray3.getJSONObject(i);
                            AddressArrayList.add(new Address(jsonObject.getString("id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("mobile"),
                                    jsonObject.getString("landmark"),
                                    jsonObject.getString("flat_no"), jsonObject.getString("street"),
                                    jsonObject.getString("pincode"), jsonObject.getString("latitude"),
                                    jsonObject.getString("longitude"), jsonObject.getString("is_default"),
                                    jsonObject.getString("city"), jsonObject.getString("city_name"),
                                    jsonObject.getString("area"), jsonObject.getString("area_name")));
                        }

                        if(AddressArrayList.size() != 0) {
                            addressList.setAdapter(new AddressAdapter(AddressShowActivity.this, AddressArrayList,
                                    R.layout.lyt_address, "cate", AddressShowActivity.this));
                            lytNoaddress.setVisibility(View.GONE);
                            lytList.setVisibility(View.VISIBLE);
                        }else{
                            lytNoaddress.setVisibility(View.VISIBLE);
                            lytList.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, AddressShowActivity.this, Constant.USER_ADDRESSES, params, false);
    }

    @Override
    public void onClick(String type, String name, String id) {
        getAddressData();
    }
}
