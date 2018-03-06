package com.example.craig.finalyearproject;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.craig.finalyearproject.model.AddressDialog;
import com.example.craig.finalyearproject.model.MyGeoLocation;
import com.example.craig.finalyearproject.model.PlaceInformation;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,AddressDialog.AddressDialogListener,DirectionCallback {
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
    private StorageReference storageReference,userRef;
    private ArrayList cordinList;
    private ListView listView;
    private MyCustomAdapter arrayAdapter;
    private double radius;
    private Polyline line=null;
    private Toolbar toolbar;
    private FirebaseUser UserID;
    private String imageName = "";
    private ImageView logo;
    private String url;
    private TextView title;
    private String getUserName;
    private String email;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String API_KEY = "AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private PlaceInformation info;
    private String placeDetails = "";
    private String serverKey = "AIzaSyDhnV49D80UcbnguzDkXyyV1nQQsh97l1c";
    //private LatLng origin = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
    private LatLng origin;
    private LatLng destination;
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

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        setUpUserName();
        setImageForToolBar();
        initToolBar();

    }

    public void setUpUserName(){
        email = UserID.getEmail().toLowerCase();
        //Query query = ref.child("UsernameInfo");
        ref.child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UsernameInfo usernameInfo = ds.getValue(UsernameInfo.class);
                    if(email.equals(usernameInfo.getEmail().toLowerCase())){
                        getUserName = usernameInfo.getUsername();
                        title.setText(getUserName);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setImageForToolBar(){
        userRef.child("profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    url = uri.toString();
                    Picasso.with(MapsActivity.this).load(url).resize(100, 100).centerCrop().into(logo);
                }catch (Exception e){
                    Toast.makeText(getApplication(),"Error while connecting to url" + url,Toast.LENGTH_LONG).show();
                    return;
                }
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
        //Log.i("MYLOCATION","LAT&LON" + mlocation.getLatitude() + mlocation.getLongitude());
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

    private void setUpFireBase(){

        refGeo = firebaseDatabase.getReference("GeoLocations");
        GeoFire geoFire = new GeoFire(refGeo);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mlocation.getLatitude(),mlocation.getLongitude()),radius);
        arrayAdapter = new MyCustomAdapter(getBaseContext(),android.R.layout.simple_list_item_1,cordinList,MapsActivity.this);
        listView.setAdapter(arrayAdapter);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                lat = location.latitude;
                lon = location.longitude;
                MyGeoLocation geoL = new MyGeoLocation();
                geoL.setKey(key);
                geoL.setLat(lat);
                geoL.setLon(lon);

                String url = BASE_URL + "placeid=" + key + "&key=" + API_KEY;
                info = getPlaceInfo(url);
                cordinList.add(info);
                num = cordinList.size();
                arrayAdapter.notifyDataSetChanged();
                Log.i("JSON","" + cordinList);
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

    private void openDialog(){
        AddressDialog addressDialog = new AddressDialog();
        addressDialog.show(getSupportFragmentManager(),"Address Dialog");
    }

    @Override
    public void getTexts(double lat,double lon,String code) {
        //Toast.makeText(this,address,Toast.LENGTH_LONG).show();
        setUpMyGeoLocation(lat,lon,code);
    }

    public void setUpMyGeoLocation(double lat,double lon,String code){

        firebaseDatabase = FirebaseDatabase.getInstance();
        refGeo = firebaseDatabase.getReference("GeoLocations");
        GeoFire geoFire = new GeoFire(refGeo);
        geoFire.setLocation(code,new GeoLocation(lat, lon));
        /*
        Geocoder geocoder = new Geocoder(this);
        if(geocoder.isPresent()){
            try {
                List<Address> addresses = geocoder.getFromLocation(lat,lon,0);
                if(addresses.size() == 0){
                    Toast.makeText(this,"Place not found",Toast.LENGTH_LONG).show();
                    return;
                }
                Address address = addresses.get(0);
                firebaseDatabase = FirebaseDatabase.getInstance();
                refGeo = firebaseDatabase.getReference("GeoLocations");
                GeoFire geoFire = new GeoFire(refGeo);
                geoFire.setLocation(address.getPhone(), new GeoLocation(address.getLatitude(), address.getLongitude()));
                for(int i=0;i<addresses.size();i++){
                    Log.i("PLACES","" + addresses.get(i));
                }

            } catch (IOException e) {
                Toast.makeText(this,"Network to geocoder not working",Toast.LENGTH_LONG).show();
            }catch (IllegalArgumentException e){
                Toast.makeText(this,"Network to geocoder not working",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"No Place entered",Toast.LENGTH_LONG).show();
        }
        */
    }

    private void setUpDoubleValue(String item){
        String newItem[];
        newItem = item.split("K",2);
        radius = Double.parseDouble(newItem[0].toString());
    }

    public void getPlaceOnMap(int position){
        map.clear();
        getCurrentLocation();
        PlaceInformation placeInformation = (PlaceInformation) listView.getItemAtPosition(position);

        double mylat = placeInformation.getLat();
        double mylon = placeInformation.getLon();
        origin = new LatLng(lat,lon);
        destination = new LatLng(mylat,mylon);
        currentPosName = placeInformation.getCompanyName();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transitMode(TransportMode.DRIVING)
                .execute(this);

        /*
        line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(lat, lon), new LatLng(mlocation.getLatitude(), mlocation.getLongitude()))
                .width(5)
                .color(Color.RED));
        moveMap();
        */
    }

    public void checkListSize(){
        if(cordinList.size() > 0){
            cordinList.clear();
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public PlaceInformation getPlaceInfo(String URL){
        PlaceInformation place = new PlaceInformation();

        try{
            placeDetails = new PlacesInfo().execute(URL).get();
            JSONObject jsonObjRoot = new JSONObject(placeDetails);

            JSONObject jsonObjRes = jsonObjRoot.getJSONObject("result");
            JSONObject geometry = jsonObjRes.getJSONObject("geometry");
            JSONObject loc = geometry.getJSONObject("location");
            JSONObject jsonOpenHours = null;
            try{
                jsonOpenHours = jsonObjRes.getJSONObject("opening_hours");
            }catch (Exception e){
                e.printStackTrace();
                place.setOpeningHours("NA");
            }
            JSONArray openingArray = jsonOpenHours.getJSONArray("weekday_text");
            JSONArray photoArray = jsonObjRes.getJSONArray("photos");
            JSONObject photoObj = photoArray.getJSONObject(0);

            //Place Details
            double lat = loc.getDouble("lat"),lon = loc.getDouble("lng");
            String phoneNum = jsonObjRes.getString("formatted_phone_number");
            String address = jsonObjRes.getString("formatted_address");
            String name = jsonObjRes.getString("name");
            String website = jsonObjRes.getString("website");
            String openHours = openingArray.getString(6);
            String photo = photoObj.getString("photo_reference");

            place.setLat(lat);
            place.setLon(lon);
            place.setCompanyName(name);
            place.setAddress(address);
            place.setPhoneNum(phoneNum);
            place.setWebsite(website);
            place.setOpeningHours(openHours);
            place.setPhoto(photo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return place;
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if(direction.isOK()){
            Route route = direction.getRouteList().get(0);
            map.addMarker(new MarkerOptions().position(origin)).setTitle(currentPosName);
            map.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            map.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            setCameraWithCoordinationBounds(route);
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this,"ERROR OCCURED",Toast.LENGTH_LONG).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


    private class PlacesInfo extends AsyncTask<String,Integer,String> {
        private ProgressDialog mProgressDialog;
        //private AsyncResult listener;
        public PlacesInfo() {
            //this.listener = listener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Create progress dialog
            mProgressDialog = new ProgressDialog(MapsActivity.this);
            // Set your progress dialog Title
            mProgressDialog.setTitle("Progress Bar Tutorial");
            // Set your progress dialog Message
            mProgressDialog.setMessage("Downloading, Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... prams) {
            try{
                URL url = new URL(prams[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int contentLength = connection.getContentLength();

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                String apiDetails = "";
                char current;
                long total =0;

                while(data != -1){
                    current = (char)data;
                    apiDetails += current;
                    data = inputStreamReader.read();
                    //total += data;
                    //Log.i("PUB","" + total);
                    //publishProgress((int) (total * 100 / contentLength));
                }
                inputStream.close();
                inputStreamReader.close();
                return apiDetails;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // Update the progress dialog
            mProgressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            //Log.i("RETURN",result);
            //listener.getResult(result);
        }

    }//end inner class

}//end outer class




