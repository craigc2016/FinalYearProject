package com.example.craig.finalyearproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.ChatMessage;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.firebase.ui.database.FirebaseListAdapter;
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
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MessageActivity extends AppCompatActivity {
    private FirebaseListAdapter<ChatMessage> adapter;
    private String companyName;
    private Toolbar toolbar;
    private ImageView logo;
    private String url;
    private String email;
    private String UserName;
    private TextView title;
    private FirebaseUser UserID;
    private DatabaseReference ref;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, userRef;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        setUpUserName();
        setImageForToolBar();
        initToolBar();
        handleMessaging();
    }

    public void handleMessaging(){
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("Username");
        final String CompanyName = intent.getStringExtra("CompanyName");
        companyName = intent.getStringExtra("CompanyName");
        Log.i("MYMESSAGE",""+companyName + CompanyName);
        displayMessages();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.msgInput);
                FirebaseDatabase.getInstance().getReference().child("Messages").child(CompanyName).push()
                        .setValue(new ChatMessage(text.getText().toString(), UserName));
                String msg = text.getText().toString();
                text.setText(" ");
                new NotificationAsync(msg,CompanyName,username).execute();
            }
        });
    }

    public void displayMessages(){
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("Messages").child(companyName)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }

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
    /*
    public static String getUserName() {
        return UserName;
    }
    */

    public void setImageForToolBar() {
        userRef.child("placeholder").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    url = uri.toString();
                    Picasso.with(MessageActivity.this).load(url).resize(100, 100).centerCrop().into(logo);
                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Error while connecting to url" + url, Toast.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_message, menu);
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
        if(id == R.id.action_fileUpload){
            startActivity(new Intent(this,UploadActivity.class));
            finish();
        }
        if(id == R.id.action_logout){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onPause(){
        super.onPause();
        finish();
    }

}//end class

class NotificationAsync extends AsyncTask<Object, Object, String> {
    String msg,segCompany,username;

    public NotificationAsync(String msg,String segCompany,String username){
        this.msg = msg;
        this.segCompany = segCompany;
        this.username = username;
    }

    @Override
    protected String doInBackground(Object... params) {
        String jsonResponse = "";
        try {
            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic OTg5YzczODktMWM2Ny00ODkxLTk1ZDUtZmM3YTRkYzcwNzll");
            con.setRequestMethod("POST");
            Log.i("VARIABLES",msg + segCompany);
            String strJsonBody = "{"
                    +   "\"app_id\": \"2b2f27d6-facb-4c36-b5d9-d12e33244e02\","
                    +   "\"included_segments\": [\""+segCompany+"\"],"
                    +   "\"data\": {\"tag\": \""+segCompany +"\"},"
                    +   "\"contents\": {\"en\": \""+msg+ "from "+username+"\"}"
                    + "}";

            Log.i("ONESIGNALMESSAGE",strJsonBody);
            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);
            Log.i("MYHTTP",""+httpResponse);
            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);
        } catch(Throwable t) {
            t.printStackTrace();
        }

        return jsonResponse;
    }
}