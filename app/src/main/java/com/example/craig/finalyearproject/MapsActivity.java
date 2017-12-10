package com.example.craig.finalyearproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,AddressDialog.AddressDialogListener, ListView.OnItemClickListener {
    private int num =0;
    private GoogleMap map;
    private Location mlocation;
    private double lat;
    private double lon;
    private String currentPosName;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refGeo;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference,userRef,imageRef;
    private ArrayList cordinList;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private double radius;
    private Polyline line=null;
    private Toolbar toolbar;
    private FirebaseUser UserID;
    private String imageName = "";
    private ImageView logo;
    private String url;
    private TextView title;
    private String getUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listView = (ListView) findViewById(R.id.list);
        cordinList = new ArrayList();
        listView.setOnItemClickListener(this);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());

        setImageForToolBar();
        initToolBar();
    }

    public void setImageForToolBar(){
        ref.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user.isProfile()){
                        imageName = user.getImage();
                        getUserName = user.getUserName();
                        title.setText(getUserName);
                        userRef.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                url = uri.toString();
                                Picasso.with(MapsActivity.this).load(url).resize(50, 50).centerCrop().into(logo);
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toast.makeText(this,"" + userRef.toString(),Toast.LENGTH_LONG).show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        logo = (ImageView) toolbar.findViewById(R.id.logo);
        title = (TextView) toolbar.findViewById(R.id.title);
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
        getCurrentLocation();
    }

    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Error permissions not granted",Toast.LENGTH_LONG).show();
            return;
        }
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
        mlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // Add a marker for your current loction
        LatLng dublin = new LatLng(mlocation.getLatitude() ,mlocation.getLongitude());
        map.addMarker(new MarkerOptions().position(dublin).title("Current Location"));
        map.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        lat = mlocation.getLatitude();
        lon = mlocation.getLongitude();
    }

    private void moveMap() {
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(lat, lon);

        //Adding marker to map
        map.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title(currentPosName)); //Adding a title

        //Moving the camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //Animating the camera
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        checkListSize();

        if(id == R.id.action_fileUpload){
            startActivity(new Intent(this,UploadActivity.class));
            finish();
        }

        if(id == R.id.action_getCurrentPosition){
            getCurrentLocation();
            moveMap();
        }
        if (id == R.id.action_address) {
            openDialog();

        }
        if(id == R.id.action_5KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
            Toast.makeText(this,"list size" + cordinList.size()+ "num " + num,Toast.LENGTH_LONG).show();
        }
        if(id == R.id.action_10KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
            //Toast.makeText(this,"list size" + getListSize()+ "num " + num,Toast.LENGTH_LONG).show();
        }
        if(id == R.id.action_15KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpFireBase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        refGeo = firebaseDatabase.getReference("Geo Locations");
        GeoFire geoFire = new GeoFire(refGeo);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mlocation.getLatitude(),mlocation.getLongitude()),radius);
        arrayAdapter = new ArrayAdapter(getBaseContext(),android.R.layout.simple_list_item_1,cordinList);
        listView.setAdapter(arrayAdapter);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                MyGeoLocation geoLocation = new MyGeoLocation();
                lat = location.latitude;
                lon = location.longitude;
                geoLocation.setKey(key);
                geoLocation.setLat(lat);
                geoLocation.setLon(lon);
                cordinList.add(geoLocation);
                arrayAdapter.notifyDataSetChanged();
                num = cordinList.size();
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

    private void openDialog(){
        AddressDialog addressDialog = new AddressDialog();
        addressDialog.show(getSupportFragmentManager(),"Address Dialog");
    }

    @Override
    public void getTexts(String address) {
        //Toast.makeText(this,address,Toast.LENGTH_LONG).show();
        setUpGeoCoding(address);
    }

    public void setUpGeoCoding(String loctionName){
        Geocoder geocoder = new Geocoder(this);
        if(geocoder.isPresent()){
            try {
                List<Address>addresses = geocoder.getFromLocationName(loctionName,5);
                if(addresses.size() == 0){
                    Toast.makeText(this,"Place not found",Toast.LENGTH_LONG).show();
                    return;
                }
                Address address = addresses.get(0);
                firebaseDatabase = FirebaseDatabase.getInstance();
                refGeo = firebaseDatabase.getReference("Geo Locations");
                GeoFire geoFire = new GeoFire(refGeo);
                geoFire.setLocation(loctionName, new GeoLocation(address.getLatitude(), address.getLongitude()));

            } catch (IOException e) {
                Toast.makeText(this,"Network to geocoder not working",Toast.LENGTH_LONG).show();
            }catch (IllegalArgumentException e){
                Toast.makeText(this,"Network to geocoder not working",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"No Place entered",Toast.LENGTH_LONG).show();
        }
    }

    private void setUpDoubleValue(String item){
        String newItem[];
        newItem = item.split("K",2);
        radius = Double.parseDouble(newItem[0].toString());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        map.clear();
        getCurrentLocation();
        MyGeoLocation geoLocation = (MyGeoLocation) listView.getItemAtPosition(position);
        lat = geoLocation.getLat();
        lon = geoLocation.getLon();
        currentPosName = geoLocation.getKey();
        line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(lat, lon), new LatLng(mlocation.getLatitude(), mlocation.getLongitude()))
                .width(5)
                .color(Color.RED));
        moveMap();
    }

    public void checkListSize(){
        if(cordinList.size() > 0){
            cordinList.clear();
            arrayAdapter.notifyDataSetChanged();
        }
    }
}



