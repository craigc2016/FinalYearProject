package com.example.craig.finalyearproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.AddressDialog;
import com.example.craig.finalyearproject.model.ImageDialog;
import com.example.craig.finalyearproject.model.User;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener,ImageDialog.ImageDialogListener{
    private Button chooseImg, uploadImg,checkImg;
    private EditText fileName;
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
    private String response;
    private boolean flag;
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
        checkImg = (Button) findViewById(R.id.btnCheckImage);
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        imgView = (ImageView)findViewById(R.id.imgView);
        fileName = (EditText) findViewById(R.id.editName);
        fileName.setVisibility(View.INVISIBLE);
        //checkImage();
        setUpUserName();
        setImageForToolBar();
        initToolBar();
        uploadImg.setEnabled(false);
        checkImg.setEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");

        chooseImg.setOnClickListener(this);
        uploadImg.setOnClickListener(this);
        checkImg.setOnClickListener(this);

    }
    public void setUpUserName(){
        email = UserID.getEmail().toLowerCase();
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
        ref.child("User").child(UserID.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(user.isProfile()){
                        imageName = user.getImage().toString();
                        //Toast.makeText(getApplicationContext(),"" + userRef.child(imageName),Toast.LENGTH_LONG).show();
                        userRef.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                try{
                                    url = uri.toString();
                                    Picasso.with(UploadActivity.this).load(url).resize(100, 100).centerCrop().into(logo);
                                }catch (Exception e){
                                    Toast.makeText(getApplication(),"Error while connecting to url" + url,Toast.LENGTH_LONG).show();
                                    return;
                                }
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


    @Override
    public void onClick(View v) {
        if(v == chooseImg){
            ChooseImage();
        }
        if(v == checkImg){
            checkImage();
        }
        if(v == uploadImg){
            UploadImage();
        }
    }

    public void ChooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);
        fileName.setVisibility(View.VISIBLE);
    }

    public void checkImage(){
        //Toast.makeText(this,""+q.toString(),Toast.LENGTH_LONG).show();
        ref.child("User").child(UserID.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    Toast.makeText(getApplication(),""+user.getImage(),Toast.LENGTH_LONG).show();
                    if(user.getImage().equals(name)){
                        Toast.makeText(getApplicationContext(),"Name of image already in use please try again",Toast.LENGTH_LONG).show();
                        Refresh();
                        break;
                    }
                    else {
                        checkImg.setEnabled(false);
                        uploadImg.setEnabled(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        checkImg.setEnabled(false);
        uploadImg.setEnabled(true);
    }

    /*
    This method is for handling the file upload of the app

     */
    public void UploadImage(){
        if(filePath != null && response != null){
            name = fileName.getText().toString();
            User user = new User();
            if(response.equals("Yes")){
                user.setProfile(true);
            }else{
                user.setProfile(false);
            }

            progressDialog.show();
            userRef = storageReference.child(UserID.getUid());
            imageRef = userRef.child(name);
            UploadTask uploadTask = imageRef.putFile(filePath);
            //user = new User(UserID.getUid(),getUserName,name);
            DatabaseReference newRef = ref.child("User").child(UserID.getUid()).push();
            user.setUserID(UserID.getUid());
            user.setUserName(getUserName);
            user.setImage(name);
            newRef.setValue(user);

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
        checkImg.setEnabled(true);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //Used to load the image using the picasso library
                Picasso.with(this).load(filePath).into(imgView);
                //Toast.makeText(this,"" + filePath,Toast.LENGTH_LONG).show();
                openDialog();
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

        return super.onOptionsItemSelected(item);
    }

    private void openDialog(){
        ImageDialog imageDialog = new ImageDialog();
        imageDialog.show(getSupportFragmentManager(),"Image Dialog");
    }

    @Override
    public void getTexts(String response) {
        this.response = response;
    }

    public void Refresh(){
        finish();
        startActivity(new Intent(UploadActivity.this,UploadActivity.class));
    }
}
