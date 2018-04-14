package com.example.craig.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.MyNotifiy;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
    private Button chooseImg, uploadImg,btnUser;
    private EditText editUser;
    private ImageView imgView;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference,userRef,imageRef;
    private String name;
    private String getUserName;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Toolbar toolbar;
    private FirebaseUser UserID;
    private String imageName = "";
    private ImageView logo;
    private String url;
    private TextView title;
    private String email;
    private ArrayList<UsernameInfo> usernameInfos;
    private ArrayList<MyNotifiy> notifiyList;
    private boolean flag;
    UsernameInfo usernameInfo;
    private DatabaseReference user,notifiy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        //create and give the folder stoarge a value using the User ID
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        //Toast.makeText(this,"" + ref.child("User").child(UserID.getUid()),Toast.LENGTH_LONG).show();
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        btnUser = (Button)findViewById(R.id.btnUsername);
        editUser = (EditText) findViewById(R.id.editUsername);
        imgView = (ImageView)findViewById(R.id.imgView);
        imgView.setImageResource(R.drawable.placeholder);
        usernameInfos = new ArrayList<>();
        notifiyList = new ArrayList<>();
        //checkImage();
        setUpUserName();
        setImageForToolBar();
        initToolBar();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        user = ref.child("UserName");
        notifiy = ref.child("Notifications");

        chooseImg.setOnClickListener(this);
        uploadImg.setOnClickListener(this);
        btnUser.setOnClickListener(this);
    }

    public void getNotifications(String username){
        notifiy.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    MyNotifiy n = ds.getValue(MyNotifiy.class);
                    Log.i("CHECKER",""+n);
                    notifiyList.add(n);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setUpUserName(){
        email = UserID.getEmail().toLowerCase();
        ref.child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UsernameInfo usernameInfo = ds.getValue(UsernameInfo.class);
                    usernameInfos.add(usernameInfo);
                    //Log.i("TESTNAME",""+t);
                    if(email.equals(usernameInfo.getEmail().toLowerCase())){
                        getUserName = usernameInfo.getUsername();
                        title.setText(getUserName);
                        editUser.setText(getUserName);
                        getNotifications(getUserName);
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
        userRef.child("placeholder").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    url = uri.toString();
                    Picasso.with(UploadActivity.this)
                            .load(url)
                            .resize(100,100)
                            .centerCrop()
                            .into(logo);
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


    @Override
    public void onClick(View v) {
        if(v == chooseImg){
            ChooseImage();
        }
        if(v == uploadImg){
            UploadImage();
        }
        if(v == btnUser){
            checkUsername();
        }
    }

    private void checkUsername(){
        UsernameInfo info = null;
        String tempName = editUser.getText().toString();
        Log.i("REF","" + user);
        String tempEmail;
        String oldName = getUserName;
        String checkName;
        for(int i=0;i<usernameInfos.size();i++){
            info = usernameInfos.get(i);
            tempEmail = info.getEmail();
            tempEmail = tempEmail.toLowerCase().trim();
            tempName  = tempName.toLowerCase().trim();
            checkName = info.getUsername();
            checkName = checkName.toLowerCase().trim();
            Log.i("CHECKNAME",checkName + "  " + tempName);
            if (checkName.equals(tempName) && !tempEmail.equals(email)){
                Toast.makeText(this,"Error username in use",Toast.LENGTH_SHORT).show();
                editUser.setText("");
                editUser.setFocusable(true);
                return;
            }
        }
        checkName = editUser.getText().toString().toLowerCase().trim();
        if(checkName.equals("")){
            Toast.makeText(this,"Error username must not be blank",Toast.LENGTH_SHORT).show();
            return;

        }
        if(!checkName.equals("")){
            info.setUsername(checkName);
            user.child(info.getKey()).removeValue();
            user.child(info.getKey()).setValue(info);
            Toast.makeText(this,"Username updated",Toast.LENGTH_LONG).show();
        }

        MyNotifiy notifiyCheck;
        for (int i=0;i<notifiyList.size();i++){
            notifiyCheck = notifiyList.get(i);
            MyNotifiy newNotify = new MyNotifiy();
            newNotify.setCompanyName(notifiyCheck.getCompanyName());
            newNotify.setSignUp(notifiyCheck.isSignUp());
            notifiy.child(oldName).removeValue();
            notifiy.child(info.getUsername()).child(notifiyCheck.getCompanyName()).setValue(newNotify);
        }

        usernameInfos.clear();
    }

    public void ChooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);
    }


    /*
    This method is for handling the file upload of the app

     */
    public void UploadImage(){
        if(filePath != null){
            name = "placeholder";
            progressDialog.show();
            userRef = storageReference.child(UserID.getUid());
            imageRef = userRef.child(name);
            UploadTask uploadTask = imageRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Upload successful ", Toast.LENGTH_SHORT).show();
                    Refresh();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(UploadActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onPause(){
        super.onPause();
        progressDialog.dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //Used to load the image using the picasso library
                Picasso.with(this).load(filePath)
                        .into(imgView);
                //Toast.makeText(this,"" + filePath,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_maps){
            startActivity(new Intent(this,MapsActivity.class));
            finish();
        }
        if(id == R.id.action_logout){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Refresh(){
        startActivity(new Intent(this,UploadActivity.class));
        finish();
    }

}
