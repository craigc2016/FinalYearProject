package com.example.craig.finalyearproject;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.craig.finalyearproject.model.AddressDialog;
import com.example.craig.finalyearproject.model.MyNotifiy;
import com.example.craig.finalyearproject.model.PlaceInformation;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
/**
This class is the main page for the app allowing for the user to interact with the map.
It will allow for the user to choose the pitches to display depending on distance.
It will then display each pitches information. It will allow for the map to draw a
path from the users location to the pitch destinantion. It will allow for the user to navigate to
the message page for that astro pitch.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, AddressDialog.AddressDialogListener, DirectionCallback{
    //Declare the variables for the class
    private int num = 0;
    private GoogleMap map;
    private double lat;
    private double lon;
    private String currentPosName;
    private String companyName;
    private DatabaseReference ref;
    private FirebaseDatabase database;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refGeo;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, userRef;
    private ArrayList cordinList;
    private double radius;
    private Toolbar toolbar;
    private FirebaseUser UserID;
    private ImageView logo;
    private String url;
    private TextView title;
    private static String UserName;
    private String email;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String API_KEY = "AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private static final String SERVERKEY = "AIzaSyDhnV49D80UcbnguzDkXyyV1nQQsh97l1c";
    private LatLng origin;
    private LatLng destination;
    private static final String IMAGE_NOT_FOUND = "https://gaygeekgab.files.wordpress.com/2015/05/wpid-photo-317.png";
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_LOCATION = 2;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<MyNotifiy> notifications;
    private DatabaseReference notificationsRef;
    private MyNotifiy myNotifiy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //reference to recycler view and set layout
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //declare the array lists
        cordinList = new ArrayList();
        notifications = new ArrayList<>();
        notifications = new ArrayList<>();
        //get instance of the storage and database
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        database = FirebaseDatabase.getInstance();
        //get references to the instances gained for firebase functions
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");

        //get reference to location provider
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setUpUserName();
        setImageForToolBar();
        initToolBar();
        //register device with One Signal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler(getApplication()))
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    /*
    This method is used to get the username for the account that is
    logged in. It checks the firebase database which holds the name.
    It gets the email linked to the account from FirebaseAuth class.
     */
    public void setUpUserName() {
        email = UserID.getEmail().toLowerCase();
        //Query query = ref.child("UsernameInfo");
        ref.child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    UsernameInfo usernameInfo = ds.getValue(UsernameInfo.class);
                    if (email.equals(usernameInfo.getEmail().toLowerCase())) {
                        UserName = usernameInfo.getUsername();
                        title.setText(UserName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Method used to get access to the username retrieved from the database
    public static String getUserName() {
        return UserName;
    }

    /*
    Method for to set the profile image for the account.
    It gets a reference to FirebaseStorage class. It will retrieve
    the image using uri. It will use the Picasso library to load and set the image.
     */
    public void setImageForToolBar() {
        userRef.child("placeholder").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    //string reference to the path of storage
                    url = uri.toString();
                    Picasso.with(MapsActivity.this)
                            .load(url)
                            .resize(100, 100)
                            .centerCrop()
                            .into(logo);
                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Error while connecting to url" + url, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*
    Method which sets up the toolbar getting access to
    the imageview and textview. Which is the placeholder for
    username and profile image.
     */
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    /*
    This method will ask the user if the app can have access to the
    device location. It will check the manifest file for the Location classes
    needed.
     */
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Error permissions not granted", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        else {
            /*
            If the user has allowed for their location to be used previously.
            It will display a marker on the map and navigate to that position.
            It will use the FusedLocationProviderClient class for to get access to the
            device location.
             */
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng dublin = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions().position(dublin).title("Current Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLng(dublin));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15));
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }

                }
            });

        }

    }
    /*
    This method is an implemented method for the check for a devices location.
    It will handle the users decision made by the user to allow for their
    devices location to be used.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng dublin = new LatLng(location.getLatitude(), location.getLongitude());
                                map.addMarker(new MarkerOptions().position(dublin).title("Current Location"));
                                map.moveCamera(CameraUpdateFactory.newLatLng(dublin));
                                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    });
                }

            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    /*
    Method which is implemented to allow for the settings
    option in the tool bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_maps, menu);
        MenuItem address = menu.findItem(R.id.action_address);
        //If statement used to check for email address which hides option in settings tab.
        if(!email.equals("craigcormack2012@hotmail.com")){
            address.setVisible(false);
        }
        return true;
    }
    /*
    Method which is implemented which handles the users input with the
    settings tab.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        checkListSize(); // method call to check list size
        map.clear(); // clears the markers on the map
        getCurrentLocation(); // method call to get a users location on the map
        if(id == R.id.action_fileUpload){
            startActivity(new Intent(this,UploadActivity.class));
            finish();
        }

        if (id == R.id.action_address){
            openDialog();

        }
        if(id == R.id.action_5KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
        }
        if(id == R.id.action_10KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
        }
        if(id == R.id.action_15KM){
            setUpDoubleValue(item.toString());
            setUpFireBase();
        }
        if(id == R.id.action_logout){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Method to check the firebase database for the users
    notifications choices.
     */
    private void getNotifications(){
        notificationsRef.child(UserName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    MyNotifiy myNotifiy = ds.getValue(MyNotifiy.class);
                    notifications.add(myNotifiy);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*
    Method which sets up the location querying checking the
    Firebase database and uses the Geo-fire library.
     */
    private void setUpFireBase(){
        getNotifications();
        //get a reference to the location in the database
        refGeo = firebaseDatabase.getReference("GeoLocations");
        /*
        Use Geo-fire library to query the location coordinates
        in the Firebase database.
         */
        GeoFire geoFire = new GeoFire(refGeo);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,lon),radius);
        //Set up the recycler view component
        adapter = new RecyclerAdapter(cordinList,this,MapsActivity.this);
        recyclerView.setAdapter(adapter);
        /*
        Method callback used to retrieve the locations within the
        radius of the user.
         */
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                String url = BASE_URL + "placeid=" + key + "&key=" + API_KEY;
                getPlaceInfo(url);
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
                //Toast.makeText(getBaseContext(),"ERROR" + error,Toast.LENGTH_LONG).show();
            }
        });
    }
    //Method used to display the dialog for entering locations to Firebase database.
    private void openDialog(){
        AddressDialog addressDialog = new AddressDialog();
        addressDialog.show(getSupportFragmentManager(),"Address Dialog");
    }

    /*
    Interface method used to retrive the details entered in the dialog.
     */
    @Override
    public void getTexts(double lat,double lon,String code) {
        setUpMyGeoLocation(lat,lon,code);
    }
    /*
    Method used to get the value for the coordinates for a location
    entered through the dialog. It sets the location as a Geo-fire location
    tagging it with a special geo location object used for querying.
     */
    public void setUpMyGeoLocation(double lat,double lon,String code){
        firebaseDatabase = FirebaseDatabase.getInstance();
        refGeo = firebaseDatabase.getReference("GeoLocations");
        GeoFire geoFire = new GeoFire(refGeo);
        geoFire.setLocation(code,new GeoLocation(lat, lon));
    }
    /*
    Method used to convert the string value of the
    settings menu which the user uses to choose the
    radius of to search in.
     */
    private void setUpDoubleValue(String item){
        String newItem[];
        newItem = item.split("K",2);
        radius = Double.parseDouble(newItem[0].toString());
    }

    /*
    This method is used to get the directions between the
    user device and their chosen destination.
     */
    public void getPlaceOnMap(int position){
        map.clear();
        getCurrentLocation();
        PlaceInformation placeInformation = (PlaceInformation) cordinList.get(position);
        double mylat = placeInformation.getLat();
        double mylon = placeInformation.getLon();
        origin = new LatLng(lat,lon);
        destination = new LatLng(mylat,mylon);
        companyName = placeInformation.getCompanyName();
        /*
        This is an API which is used to get the location details
        between the user and destination. It uses a third party library to
        handle this.
         */
        GoogleDirection.withServerKey(SERVERKEY)
                .from(origin)
                .to(destination)
                .transitMode(TransportMode.WALKING)
                .execute(this);
    }
    /*
    Method used to check the ArrayList
     */
    public void checkListSize(){
        if(cordinList.size()> 0){
            cordinList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    /*
    Method which is used to retrieve the details for locations
    from Google Places API. It will use the networking library volley
    to make the requests. It will parse the returned JSON Objects.
     */
    public void getPlaceInfo(String URL){
        //Volley library request
       JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Create the model class object used to store the details returned
                        PlaceInformation place = new PlaceInformation();
                        /*
                        Parse the returned request JSON objects. This gives the
                        details of each astro pitch.
                         */
                        try{
                            /*
                            Create JSON objects to hold the returned details
                             */
                            JSONObject jsonObjRes = response.getJSONObject("result");
                            JSONObject geometry = jsonObjRes.getJSONObject("geometry");
                            JSONObject loc = geometry.getJSONObject("location");
                            JSONObject jsonOpenHours = jsonObjRes.optJSONObject("opening_hours");
                            JSONArray openingArray = jsonOpenHours.getJSONArray("weekday_text");
                            JSONArray photoArray;
                            JSONObject photoObj = null;
                            String photo;
                            try{
                                photoArray = jsonObjRes.getJSONArray("photos");
                                photoObj = photoArray.getJSONObject(0);
                            }catch (Exception e){
                                photo = IMAGE_NOT_FOUND;
                                Log.i("ERRORI",photo);
                                e.printStackTrace();
                            }
                            photo = photoObj.getString("photo_reference");
                            boolean openNow = jsonOpenHours.getBoolean("open_now");
                            String openNowStr = "";
                            if (openNow == true){
                                openNowStr = "YES";
                            }else {
                                openNowStr = "NO";
                            }
                            /*
                            Create variables to store the astro pitch details
                             */
                            double lat = loc.getDouble("lat"),lon = loc.getDouble("lng");
                            String phoneNum = jsonObjRes.getString("formatted_phone_number");
                            String address = jsonObjRes.getString("formatted_address");
                            String name = jsonObjRes.getString("name");
                            String website = jsonObjRes.getString("website");
                            String openHours="";
                            for(int i =0;i<openingArray.length();i++){
                                openHours += "\n" + openingArray.getString(i);
                            }
                            //Set the fields of the model class
                            place.setLat(lat);
                            place.setLon(lon);
                            place.setCompanyName(name);
                            place.setAddress(address);
                            place.setPhoneNum(phoneNum);
                            place.setWebsite(website);
                            place.setOpeningHours(openHours);
                            place.setPhoto(photo);
                            place.setOpenNow(openNowStr);
                            /*
                            Check the status of the notifications options.
                            Then set the notifications action to the current model object.
                             */
                            for (int i = 0;i<notifications.size();i++){
                                myNotifiy = notifications.get(i);
                                if(place.getCompanyName().equals(myNotifiy.getCompanyName())){
                                    place.setChecked(myNotifiy.isSignUp());
                                    Log.i("MYNOTIFY","" +myNotifiy.isSignUp());
                                }
                            }
                            cordinList.add(place);
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        //Make the volley request
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    /*
    Implemented third party library which draws the path between
    the user device and destination location. It will draw the maker along
    the maps roads.
     */
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if(direction.isOK()){
            Route route = direction.getRouteList().get(0);
            map.addMarker(new MarkerOptions().position(origin));
            map.addMarker(new MarkerOptions().position(destination)).setTitle(companyName);
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            map.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            setCameraWithCoordinationBounds(route);
        }
    }
    /*
    Implemented method which occurs for an error.
     */
    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this,"ERROR OCCURED",Toast.LENGTH_LONG).show();
    }
    /*
    Method which is used to carry out the animation which zooms out the
    map view.
     */
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
    /*
    Implemented method used for when the app pauses
    Does not allow for handing in the background.
     */
    protected void onPause(){
        super.onPause();
        finish();
    }

}//end outer class
