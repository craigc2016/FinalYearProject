package com.example.craig.finalyearproject;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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



public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button chooseImg, uploadImg;
    private EditText fileName;
    private ImageView imgView;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String name;
    private String getUserName;
    private EditText username;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Button upload;
    private FirebaseAuth a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        upload = (Button) findViewById(R.id.UploadPage);
        upload.setOnClickListener(this);
        imgView = (ImageView)findViewById(R.id.imgView);
        fileName = (EditText) findViewById(R.id.editName);
        username = (EditText) findViewById(R.id.username);

    }

    public void onClick(View view){
        if(view == upload){
            UploadPage();
        }
    }

    public void UploadPage(){
        startActivity(new Intent(this,UploadActivity.class));
        finish();
    }

}
