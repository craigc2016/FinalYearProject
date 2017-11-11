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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_register);

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

    private void userReg(){
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();


        if (TextUtils.isEmpty(emailText)){
            //email is empty
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }

        if (TextUtils.isEmpty(passwordText)){
            //email is empty
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

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
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

}
