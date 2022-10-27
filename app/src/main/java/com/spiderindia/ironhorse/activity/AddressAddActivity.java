package com.spiderindia.ironhorse.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Constant;
import com.spiderindia.ironhorse.helper.GPSTracker;
import com.spiderindia.ironhorse.helper.Session;
import com.spiderindia.ironhorse.helper.VolleyCallback;
import com.spiderindia.ironhorse.model.CityAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressAddActivity extends AppCompatActivity implements OnMapReadyCallback {

    static Activity activity;
    static Session session;
    Button btnsubmit;
    ImageView btnback;

    EditText edtname,edtmobile,edtaddress,edtPinCode,edtLandMArk;

    TextView tvUpdate,tvCurrent;

    String cityId = "0", areaId = "0", city, area ;
    ArrayList<CityAddress> cityArrayList, areaList;


    AppCompatSpinner cityspinner, areaSpinner ;
    SupportMapFragment mapFragment;
    double latitude = 0.0, longitude = 0.0;

    String strAdddata = "", id = "", strmap = "0";

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaddress);

        session = new Session(AddressAddActivity.this);

        gps = new GPSTracker(AddressAddActivity.this);

        btnsubmit = findViewById(R.id.btnsubmit);
        btnback = findViewById(R.id.btnback);

        tvUpdate = findViewById(R.id.tvUpdate);
        edtLandMArk = findViewById(R.id.edtLandMArk);

        edtname = findViewById(R.id.edtname);
        edtmobile = findViewById(R.id.edtmobile);
        edtaddress = findViewById(R.id.edtaddress);
        edtPinCode = findViewById(R.id.edtPinCode);
        areaSpinner = findViewById(R.id.areaSpinner);
        cityspinner = findViewById(R.id.cityspinner);

        tvCurrent = findViewById(R.id.tvCurrent);

        cityArrayList = new ArrayList<>();
        areaList = new ArrayList<>();
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApiConfig.isGPSEnable(AddressAddActivity.this)) {
                    strmap = "1";
                    if(strAdddata.equals("Edit")) {

//                        String strlat = getIntent().getStringExtra("latitute").toString().trim();
//                        String strlong =getIntent().getStringExtra("longtitude").toString().trim();
//                        Intent intent = new Intent(AddressAddActivity.this, MapActivity.class);
//                        intent.putExtra("PAGE", "ADDRESSEDIT");
//                        intent.putExtra("LAT",strlat);
//                        intent.putExtra("LONG", strlong);
//                        startActivity(intent);

                       startActivity(new Intent(AddressAddActivity.this, MapsActivity.class).putExtra("Edit", "Edit"));
                    }else{
//                        String strlat = getIntent().getStringExtra("latitute").toString().trim();
//                        String strlong =getIntent().getStringExtra("longtitude").toString().trim();
//                        Intent intent = new Intent(AddressAddActivity.this, MapActivity.class);
//                        intent.putExtra("PAGE", "ADDRESS");
//                        intent.putExtra("LAT",strlat);
//                        intent.putExtra("LONG", strlong);
//                        startActivity(intent);

                       startActivity(new Intent(AddressAddActivity.this, MapsActivity.class));
                    }
                }
                else {
                    ApiConfig.displayLocationSettingsRequest(AddressAddActivity.this);
                }
            }
        });

        try {
            edtname.setText(getIntent().getStringExtra("name").toString().trim());
            edtPinCode.setText(getIntent().getStringExtra("pincode").toString().trim());
            edtLandMArk.setText(getIntent().getStringExtra("landmark").toString().trim());
            areaId = getIntent().getStringExtra("area_id").toString().trim();
            cityId = getIntent().getStringExtra("city_id").toString().trim();
            edtaddress.setText(getIntent().getStringExtra("street").toString().trim());
            edtmobile.setText(getIntent().getStringExtra("mobile").toString().trim());
          session.setData(Session.KEY_LATITUDE_MANAGE_ADDRESS,getIntent().getStringExtra("latitute").toString().trim());
          session.setData(Session.KEY_LONGITUDE_MANAGE_ADDRESS,getIntent().getStringExtra("longtitude").toString().trim());
            strAdddata = "Edit";
            id = getIntent().getStringExtra("id").toString().trim();
            // Toast.makeText(getApplicationContext(), areaId + ", "+ cityId, Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            ex.printStackTrace();
            strAdddata = "New";
            //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApiConfig.CheckValidattion(edtname.getText().toString().trim(), false, false)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage(getString(R.string.enter_name))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    edtname.requestFocus();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else if (ApiConfig.CheckValidattion(edtmobile.getText().toString().trim(), false, false)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage(getString(R.string.enter_mobile_no))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    edtmobile.requestFocus();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }  else if (ApiConfig.CheckValidattion(edtaddress.getText().toString().trim(), false, false)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage("Enter Street or Colony")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    edtaddress.requestFocus();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else if (ApiConfig.CheckValidattion(edtPinCode.getText().toString().trim(), false, false)) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage(getString(R.string.enter_pincode))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    edtPinCode.requestFocus();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else if (cityId.equals("0")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage(getString(R.string.selectcity))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                } else if (areaId.equals("0")) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                    builder.setMessage(getString(R.string.selectarea))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
               else {
                    Map<String, String> params = new HashMap<String, String>();
                    if(strAdddata.equals("Edit")){
                        params.put("type", "update_address");
                    }else {
                        params.put("type", "add_address");
                    }

                    if(strmap.equals("0")){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddressAddActivity.this);
                        builder.setMessage("Please Choose the delivery location on the map?")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                        strmap = "1";
                                        if(strAdddata.equals("Edit")) {
//                                            Intent intent = new Intent(AddressAddActivity.this, MapActivity.class);
//                                            intent.putExtra("PAGE", "ADDRESS");
//                                            intent.putExtra("LAT","0");
//                                            intent.putExtra("LONG", "0");
//                                            startActivity(intent);


                                            startActivity(new Intent(AddressAddActivity.this, MapsActivity.class).putExtra("Edit", "Edit"));
                                        }else{

//                                            Intent intent = new Intent(AddressAddActivity.this, MapActivity.class);
//                                            intent.putExtra("PAGE", "ADDRESS");
//                                            intent.putExtra("LAT","0");
//                                            intent.putExtra("LONG", "0");
//                                            startActivity(intent);

                                       startActivity(new Intent(AddressAddActivity.this, MapsActivity.class));
                                        }
                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                    }else {
                        params.put("city_id", cityId);
                        params.put("area_id", areaId);
                        params.put("street", edtaddress.getText().toString().trim());
                        params.put("pincode", edtPinCode.getText().toString().trim());



                        params.put("latitude", String.valueOf(latitude));
                        params.put("longitude", String.valueOf(longitude));
                        params.put("landmark", edtLandMArk.getText().toString().trim());
                        params.put("mobile", edtmobile.getText().toString().trim());
                        params.put("name", edtname.getText().toString().trim());
                        params.put("id", id);
                        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                        ApiConfig.RequestToVolley(new VolleyCallback() {
                            @Override
                            public void onSuccess(boolean result, String response) {
                                if (result) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        if (!json.getBoolean(Constant.ERROR)) {
                                            Toast.makeText(getApplicationContext(), json.getString("message").toString().trim(), Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        } else {
                                            Toast.makeText(getApplicationContext(), json.getString("message").toString().trim(), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, AddressAddActivity.this, Constant.USER_ADDRESSES, params, false);
                    }
                }
            }
        });

        SetSpinnerData();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(strAdddata.equals("Edit")){
            latitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE_MANAGE_ADDRESS));
            longitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE_MANAGE_ADDRESS));
            tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, AddressAddActivity.this));
        }else {
            latitude = gps.latitude;
            longitude = gps.longitude;
            tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, AddressAddActivity.this));
        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(AddressAddActivity.this);
            }
        }, 1000);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
//        double saveLatitude = latitude;
//        double saveLongitude = longitude;
        double saveLatitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE_MANAGE_ADDRESS));
        double saveLongitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE_MANAGE_ADDRESS));
        mMap.clear();

        LatLng latLng = new LatLng(saveLatitude, saveLongitude);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }


    private void SetSpinnerData() {
        Map<String, String> params = new HashMap<String, String>();
        cityArrayList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        int pos = 0;
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            cityArrayList.add(0, new CityAddress("0", getString(R.string.select_city)));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cityArrayList.add(new CityAddress(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME)));
                                if (jsonObject.getString(Constant.ID).equals(cityId))
                                    pos = i;
                            }
                            cityspinner.setAdapter(new ArrayAdapter<>(AddressAddActivity.this, R.layout.spinner_item, cityArrayList));
                            cityspinner.setSelection(pos + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, AddressAddActivity.this, Constant.CITY_URL, params, false);

        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = cityArrayList.get(position).getCity_id();
                city = cityspinner.getSelectedItem().toString();
                SetAreaSpinnerData(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void SetAreaSpinnerData(String cityId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.CITY_ID, cityId);
        areaList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {

                        JSONObject objectbject = new JSONObject(response);
                        int pos = 0;
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            areaList.add(0, new CityAddress("0", getString(R.string.select_area),""));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                areaList.add(new CityAddress(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), ""));
                                if (jsonObject.getString(Constant.ID).equals(areaId))
                                    pos = i;

                            }
                            areaSpinner.setAdapter(new ArrayAdapter<CityAddress>(AddressAddActivity.this, R.layout.spinner_item, areaList));
                            areaSpinner.setSelection(pos + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, AddressAddActivity.this, Constant.GET_AREA_BY_CITY, params, false);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaId = areaList.get(position).getCity_id();
                area = areaSpinner.getSelectedItem().toString();
                // edtPinCode.setText(areaList.get(position).getPincode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
