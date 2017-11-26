package com.example.craig.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        imgView = (ImageView)findViewById(R.id.imgView);
        fileName = (EditText) findViewById(R.id.editName);
        fileName.setVisibility(View.INVISIBLE);
        username = (EditText) findViewById(R.id.username);
        username.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");

        chooseImg.setOnClickListener(this);

        uploadImg.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == chooseImg){
            ChooseImage();
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
        username.setVisibility(View.VISIBLE);
    }

    public void UploadImage(){
        if(filePath != null){
            progressDialog.show();
            name = fileName.getText().toString();
            getUserName = username.getText().toString();
            FirebaseUser UserID = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference childRef = storageReference.child(name+".jpg");
            UploadTask uploadTask = childRef.putFile(filePath);
            User user = new User(UserID.getUid(),getUserName,name);
            DatabaseReference newR = ref.child("User").push();
            newR.setValue(user);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Upload successful " + name, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(UploadActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
