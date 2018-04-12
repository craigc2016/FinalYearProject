package com.example.craig.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.UsernameInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<UsernameInfo> list = new ArrayList<>();
    private EditText email,password,username;
    private Button signIn;
    private Button clear;
    private TextView msg;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference ref,userNameRef;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get Reference to Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userNameRef = ref.child("UserName");
        /*
         */
        if(firebaseAuth != null){
            //HomeScreen();
        }

        //Get references to the UI components
        username = (EditText) findViewById(R.id.editUserName);
        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPass);
        signIn = (Button) findViewById(R.id.btnSignIn);
        clear = (Button) findViewById(R.id.btnClear);
        msg = (TextView) findViewById(R.id.txtMsg);
        progressDialog = new ProgressDialog(this);

        //get the in use usernames
        getUserNames();

        //Add listener to buttons and text view
        signIn.setOnClickListener(this);
        clear.setOnClickListener(this);
        msg.setOnClickListener(this);

    }

    /*
    Implemented method that handle the user input with buttons
    and the text in the layout.
     */
    @Override
    public void onClick(View view) {
        if(view == signIn){
            userReg();
        }
        else if (view == msg){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        else if(view == clear){
            email.setText("");
            password.setText("");
            email.setFocusable(true);
        }
    }

    /*
    This method downloads the usernames stored in the
    firebase database. So when a user chooses a username it makes
    sure it does not clash with one already in use.
     */
    private void getUserNames(){
        Query query = userNameRef;
        query.orderByKey().addValueEventListener(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    UsernameInfo u = ds.getValue(UsernameInfo.class);
                    list.add(u);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    This method is used fot the register functionality of the app.
    It takes the user information from the textfields. It then checks
    the usernames list to check if one is in use. If so it will then
    display the user with an error message. If a valid username and email information
    is entered it will register the account and bring the user to the login page.
     */
    private void userReg(){
        final String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        final String usernameText = username.getText().toString().trim();

        if (TextUtils.isEmpty(usernameText)){
            //usernameInfo is empty
            Toast.makeText(this,"Please enter usernameInfo",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(passwordText)){
            //password is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(emailText)){
            //email is empty
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }
        UsernameInfo info;
        for (int i =0;i<list.size();i++){
            info = list.get(i);
            if(info.getUsername().equals(usernameText) ){
                Toast.makeText(this,"Error username in use",Toast.LENGTH_SHORT).show();
                username.setText("");
                username.setFocusable(true);
                return;
            }
        }

        //If validations are ok
        //show progress bar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            //User is successfully registered and logged in
                            DatabaseReference newRef = userNameRef.push();
                            UsernameInfo usernameInfo = new UsernameInfo(usernameText,emailText,newRef.getKey());
                            newRef.setValue(usernameInfo);
                            Toast.makeText(RegisterActivity.this, usernameText , Toast.LENGTH_SHORT).show();
                            LoginScreen();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Failed to register" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void onPause(){
        super.onPause();
        progressDialog.dismiss();
    }


    private void LoginScreen(){
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

}
