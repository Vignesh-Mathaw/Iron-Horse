package com.spiderindia.ironhorse.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.spiderindia.ironhorse.R;
import com.spiderindia.ironhorse.helper.ApiConfig;
import com.spiderindia.ironhorse.helper.Session;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    private GoogleApiClient googleApiClient;
    private double longitude, c_longitude, c_latitude;
    private double latitude;
    private GoogleMap mMap;
    TextView text, tvSatellite, tvStreet;
    Toolbar toolbar;
    Session session;
    FloatingActionButton fabSatellite, fabStreet, fabCurrent;
    int mapType = GoogleMap.MAP_TYPE_NORMAL;
    SupportMapFragment mapFragment;
    public boolean isCurrent;


    String Bindlat,Bindlong;
    MarkerOptions markerOptions;
    String FromPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.select_location));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(MapActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        markerOptions = new MarkerOptions();

        text = findViewById(R.id.tvLocation);
        fabSatellite = findViewById(R.id.fabSatellite);
        fabCurrent = findViewById(R.id.fabCurrent);
        fabStreet = findViewById(R.id.fabStreet);
        fabSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                mapFragment.getMapAsync(MapActivity.this);
            }
        });
        fabStreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                mapFragment.getMapAsync(MapActivity.this);
            }
        });
        fabCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(c_latitude, c_longitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                markerOptions.position(latLng);
                /* mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(getString(R.string.current_location)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                */
                //text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
                text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, MapActivity.this));
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(MapActivity.this);
            }
        }, 1000);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

       /* mMap = googleMap;
        mMap.clear();
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setMapType(mapType);
        mMap.setOnMarkerDragListener(this);

        mMap.setOnMapLongClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                //Moving the map
                mMap.clear();
                moveMap(false);
            }
        });
        // text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
        text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, MapActivity.this));*/
        try {
            mMap = googleMap;


            mMap = googleMap;
            mMap.clear();
            LatLng latLng;
            latLng = new LatLng(latitude, longitude);

            // Add a marker in Sydney and move the camera
            Log.i("lat and lan", "lat  " + latitude + " and lan " + longitude);
            LatLng Spider = new LatLng(latitude, longitude);
            // LatLng Spider = new LatLng(13.015303, 80.222799);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Spider));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Spider, 18));
            mMap.setMapType(mapType);
            markerOptions.position(Spider);


            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLng latLng = mMap.getCameraPosition().target;
                    // Config.writeLog("Center " + String.valueOf(nn));
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    //saveLocation(latitude, longitude);
                    text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, MapActivity.this));
                }
            });



            text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, MapActivity.this));
        }catch (Exception ex) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MapActivity.this);
            builder.setMessage(ex.getMessage())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
        }

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations, this can be null.
                if (location != null) {
                    c_longitude = location.getLongitude();
                    c_latitude = location.getLatitude();


//                    if (Bindlat.equals("0") || Bindlong.equals("0")) {
//                        longitude = location.getLongitude();
//                        latitude = location.getLatitude();
//                    } else {
//                      longitude = Double.parseDouble(Bindlat);
//                      latitude = Double.parseDouble(Bindlong);
//                    }

                    if (session.getCoordinates(Session.KEY_LATITUDE).equals("0") || session.getCoordinates(Session.KEY_LONGITUDE).equals("0")) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    } else {
                      longitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE));
                      latitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE));
                    }
                    moveMap(true);
                }
            }
        });

    }

    private void moveMap(boolean isfirst) {


        LatLng latLng = new LatLng(latitude, longitude);


        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.set_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        if (isfirst)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        text.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, MapActivity.this));
        //  text.setText("Latitude - " + latitude + "\nLongitude - " + longitude);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        saveLocation(latitude, longitude);
        //Moving the map
        moveMap(false);

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        moveMap(false);
    }

    public void saveLocation(double latitude, double longitude) {
       session.setData(Session.KEY_LATITUDE, String.valueOf(latitude));
       session.setData(Session.KEY_LONGITUDE, String.valueOf(longitude));
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        //  saveLocation(latitude, longitude);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveLocation(latitude, longitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
    }

    public void OnLocationClick(View view) {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void UpdateLocation(View view) {
        onBackPressed();
    }
}
