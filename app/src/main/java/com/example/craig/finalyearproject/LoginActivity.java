package com.example.craig.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class is for the login feature of the App. It will allow the user to
 * enter their email and password to get access to the App. It will validate input
 * from the users and compare to the FirebaseAuth class.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    //Declare variables for the class
    private EditText email;
    private EditText password;
    private Button signIn;
    private Button clear;
    private TextView msg;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Reference to Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        /*
         */
        if(firebaseAuth != null){
            //HomeScreen();
        }

        //Get references to the UI components
        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPass);
        signIn = (Button) findViewById(R.id.btnSignIn);
        clear = (Button) findViewById(R.id.btnClear);
        msg = (TextView) findViewById(R.id.txtMsg);
        progressDialog = new ProgressDialog(this);

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
            userLogin();
        }
        else if(view == clear){
            email.setText("");
            password.setText("");
            email.setFocusable(true);
        }
        else if(view == msg){
            RegisterScreen();
        }
    }

    private void userLogin(){
        final String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailText)){
            //email is empty
            Toast.makeText(this,"Please enter email", Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(passwordText)){
            //email is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            //stopping the function execution further
            return;
        }
        //If validations are ok
        //show progress bar
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailText,passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            MapScreen();
                            //UploadScreen();
                        }else {
                            Toast.makeText(getApplicationContext(), "ERROR USER NOT FOUND PLEASE TRY AGAIN!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
    /*
    Implemented method which stops the progress dialog from
    hanging in the background
     */
    protected void onPause(){
        super.onPause();
        progressDialog.dismiss();
    }

    /*
    Method which holds code to access the register page
     */
    private void RegisterScreen(){
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }
    /*
    Method which holds code to access the Map page
     */
    private void MapScreen(){
        startActivity(new Intent(this,MapsActivity.class));
        finish();
    }



}
