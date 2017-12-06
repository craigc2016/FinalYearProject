package com.example.craig.finalyearproject;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap map;
    private Location mlocation;
    private double lat;
    private double lon;
    private Button btnFindLocation;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private Button btnFindDes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnFindLocation = (Button) findViewById(R.id.btnLoc);
        btnFindDes = (Button) findViewById(R.id.btnFind);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnFindLocation.setOnClickListener(this);
        btnFindDes.setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng dublin = new LatLng(53.3882 , -6.1992);
        map.addMarker(new MarkerOptions().position(dublin).title("Marker in Dublin"));
        map.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Error permissions not granted",Toast.LENGTH_LONG).show();
            map.setMyLocationEnabled(true);
            return;
        }
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
        mlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        lat = mlocation.getLatitude();
        lon = mlocation.getLongitude();
        Toast.makeText(this,"lat = " +lat + "lon = " + lon,Toast.LENGTH_LONG).show();
        moveMap();
    }

    private void moveMap() {
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(lat, lon);

        //Adding marker to map
        map.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void setUpFireBase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Geo Locations");
        GeoFire geoFire = new GeoFire(ref);
        //geoFire.setLocation("", new GeoLocation(53.347860, -6.27976));
        double getLat = lat,getLon = lon;
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(getLat,getLon),0.1);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Toast.makeText(getApplicationContext(),"Key " + key + "lat " + location.latitude + "lon " + location.longitude,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == btnFindLocation){
            //getCurrentLocation();
            getCurrentLocation();
        }
        if(v == btnFindDes){
            setUpFireBase();
        }
    }
}



