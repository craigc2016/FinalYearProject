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

/**
 * This class is used by the user to handle the choice of username and profile picture.
 * It will allow for the setting of a new profile or username.
 */
public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
    //Declare the variables for the
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
    private UsernameInfo usernameInfo;
    private DatabaseReference user,notifiy;
    /**
     * Get references to the Firebase class like storage, authentication and database.
     * It gets a reference to the widgets for the profile page. It will declare the
     * array lists used. It will create and set the message of the progress dialog used.
     * It will attached the listeners to the widgets. It makes the method calls which
     * are created below which perform some action like uploading the image.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        /**
         * Get an instance of Firebase and get the references to the
         * storage, database and authentication classes needed for the
         * backend functions.
         */
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        user = ref.child("UserName");
        notifiy = ref.child("Notifications");
        /**
         * Get the references to the widgets of the
         * profile page. And set the image of the image view
         */
        chooseImg = (Button)findViewById(R.id.chooseImg);
        uploadImg = (Button)findViewById(R.id.uploadImg);
        btnUser = (Button)findViewById(R.id.btnUsername);
        editUser = (EditText) findViewById(R.id.editUsername);
        imgView = (ImageView)findViewById(R.id.imgView);
        imgView.setImageResource(R.drawable.placeholder);
        /**
         * Create an instance of the Array lists used.
         */
        usernameInfos = new ArrayList<>();
        notifiyList = new ArrayList<>();

        /**
         * Method calls which perform the actions
         * of the profile page.
         */
        setUpUserName();
        setImageForToolBar();
        initToolBar();

        /**
         * Create an instance of progress dialog and set the message
         */
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading....");
        /**
         * Attach the listeners for the widgets
         */
        chooseImg.setOnClickListener(this);
        uploadImg.setOnClickListener(this);
        btnUser.setOnClickListener(this);
    }

    /**
     * Method used to get the states of the notification widgets
     * @param username
     */
    public void getNotifications(String username){
        notifiy.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    MyNotifiy n = ds.getValue(MyNotifiy.class);
                    notifiyList.add(n);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /**
     * This method is used to get the username for the account that is
     * logged in. It checks the firebase database which holds the name.
     * It gets the email linked to the account from FirebaseAuth class.
     */
    public void setUpUserName(){
        email = UserID.getEmail().toLowerCase();
        ref.child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UsernameInfo usernameInfo = ds.getValue(UsernameInfo.class);
                    usernameInfos.add(usernameInfo);
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
    /**
     * Method for to set the profile image for the account.
     * It gets a reference to FirebaseStorage class. It will retrieve
     * the image using uri. It will use the Picasso library to load and set the image.
    */
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

    /**
     * Method which sets up the toolbar getting access to
     * the imageview and textview. Which is the placeholder for
     * username and profile image.
     */
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
     * Implemented method which handles the button
     * clicks.
     * @param v
     */
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

    /**
     * This method is used to update the Database notifications username.
     * Each notification state is stored under the username. So when the
     * username is changed this method updates that username
     */
    private void checkUsername(){
        /**
         * Declare the variables used by the method
         */
        UsernameInfo info = null;
        String tempName = editUser.getText().toString();
        String tempEmail;
        String oldName = getUserName;
        String checkName;
        /**
         * Loop used to check the username entered is not in
         * use. If it is an error will be thrown and the
         * user must re-enter a username not in use.
         */
        for(int i=0;i<usernameInfos.size();i++){
            info = usernameInfos.get(i);
            tempEmail = info.getEmail();
            tempEmail = tempEmail.toLowerCase().trim();
            tempName  = tempName.toLowerCase().trim();
            checkName = info.getUsername();
            checkName = checkName.toLowerCase().trim();
            /**
             * If statement which checks for the username in use.
             */
            if (checkName.equals(tempName) && !tempEmail.equals(email)){
                Toast.makeText(this,"Error username in use",Toast.LENGTH_SHORT).show();
                editUser.setText("");
                editUser.setFocusable(true);
                return;
            }
        }
        /**
         * assigns the username to a variable and checks if its
         * blank if so an error message will be thrown.
         */
        checkName = editUser.getText().toString().toLowerCase().trim();
        if(checkName.equals("")){
            Toast.makeText(this,"Error username must not be blank",Toast.LENGTH_SHORT).show();
            return;

        }
        /**
         * If the username is not empty the username will be updated.
         * It will then delete the old username and insert the new one.
         */
        if(!checkName.equals("")){
            info.setUsername(checkName);
            user.child(info.getKey()).removeValue();
            user.child(info.getKey()).setValue(info);
            Toast.makeText(this,"Username updated",Toast.LENGTH_LONG).show();
        }

        /**
         * This loop will then loop over the notification states
         * and set them to be placed under the new username.
         */
        MyNotifiy notifiyCheck;
        for (int i=0;i<notifiyList.size();i++){
            notifiyCheck = notifiyList.get(i);
            MyNotifiy newNotify = new MyNotifiy();
            newNotify.setCompanyName(notifiyCheck.getCompanyName());
            newNotify.setSignUp(notifiyCheck.isSignUp());
            notifiy.child(oldName).removeValue();
            notifiy.child(info.getUsername()).child(notifiyCheck.getCompanyName()).setValue(newNotify);
        }
        //clear the array list
        usernameInfos.clear();
    }

    /**
     * Method used to allow the user to choose
     * an image from their device. It uses intents
     * to handle this.
     */
    public void ChooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_REQUEST);
    }


    /**
     * This method is for handling the file upload of the app.
     * It will get a reference to the folder which matches
     * the user logged in to the App. It will then store this
     * in the Firebase storage tab.
     */
    public void UploadImage(){
        if(filePath != null){
            /**
             * Name the image which is retrieved
             * display the progress dialog
             */
            name = "placeholder";
            progressDialog.show();
            /**
             * Get the references needed to store the
             * image to Firebase storage tab.
             */
            userRef = storageReference.child(UserID.getUid());
            imageRef = userRef.child(name);

            /**
             * Upload task which is needed to upload the image
             * file to Firebase stoarge tab. It will use an onSucsessListener
             * to handle the upload
             */
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

    /**
     * Method used to not allow the progress
     * dialog to hang in the background
     */
    protected void onPause(){
        super.onPause();
        progressDialog.dismiss();
    }

    /**
     * Method which is used to display the image chosen to
     * the image view on the profile page. It is an implemented method
     * for callback on the activity file.
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * Method which is implemented to allow for the settings
     * option in the tool bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_upload, menu);
        return true;
    }

    /**
     * Method which is implemented which handles the users input with the
     * settings tab.
    */
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

    /**
     * Used to update the page for changes to
     * take place.
     */
    private void Refresh(){
        startActivity(new Intent(this,UploadActivity.class));
        finish();
    }

}
