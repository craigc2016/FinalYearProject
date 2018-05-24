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
/**
This class is for the handling of the messaging feature in the App.
It will create the references needed to connect to the Firebase database which
is used to hold the messages and serve them to users of the App. It will also handle
the sending of notifications through the web service One Signal to all users signed up for
notifications from each message page.
*/

public class MessageActivity extends AppCompatActivity {
    //Declare the variables needed for the class
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
        /*
        Get instances of the Firebase features and make a reference
        to these also.
         */
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl("gs://finalyearproject-894cb.appspot.com");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        UserID = FirebaseAuth.getInstance().getCurrentUser();
        userRef = storageReference.child(UserID.getUid());
        /*
        Method calls for actions from this class
         */
        setUpUserName();
        setImageForToolBar();
        initToolBar();
        handleMessaging();
    }
    /*
    This method is used to handle the sending of a message.
    From the users of the App.
     */
    public void handleMessaging(){
        /*
        Get reference to floating button widget. Set up an Intent object and
        retrieve values from it.
         */
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("Username");
        final String CompanyName = intent.getStringExtra("CompanyName");
        companyName = intent.getStringExtra("CompanyName");
        //Method call to display the messages saved in the database
        displayMessages();
        /*
        Button click which handles the messaging sending by the users of
        the App.
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.msgInput);
                FirebaseDatabase.getInstance().getReference().child("Messages").child(CompanyName).push()
                        .setValue(new ChatMessage(text.getText().toString(), UserName));
                String msg = text.getText().toString();
                text.setText(" ");
                /*
                Create an instance of the inner class which handles
                the sending of the One Signal notifications.
                */
                new NotificationAsync(msg,CompanyName,username).execute();
            }
        });
    }

    /*
    Method which handles the retrival of the messages stored for that message page
     */
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
        //set the adapter for the messages needed to display in the list view
        listOfMessages.setAdapter(adapter);
    }
    /*
    This method is used to get the username for the account that is
    logged in. It checks the firebase database which holds the name.
    It gets the email linked to the account from FirebaseAuth class.
     */
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
    Method for to set the profile image for the account.
    It gets a reference to FirebaseStorage class. It will retrieve
    the image using uri. It will use the Picasso library to load and set the image.
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

    /*
    Method which sets up the toolbar getting access to
    the imageview and textview. Which is the placeholder for
    username and profile image.
     */
    public void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        logo = (ImageView) toolbar.findViewById(R.id.logo);
        title = (TextView) toolbar.findViewById(R.id.title);
    }
    /*
    Method which is implemented to allow for the settings
    option in the tool bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_message, menu);
        return true;
    }

    /*
   Method which is implemented which handles the users input with the
   settings tab.
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

/**
Inner class which contains mostly boiler plate code given by One Signal needed
to send the notifications to registered Users.
 */
class NotificationAsync extends AsyncTask<Object, Object, String> {
    //declare variables used for message, pitch name and username
    private String msg,segCompany,username;

    /*
    Constructor used to take in variables and set there values
    in the class.
     */
    public NotificationAsync(String msg,String segCompany,String username){
        this.msg = msg;
        this.segCompany = segCompany;
        this.username = username;
    }

    /*
    Implemented method which is used in the Async Task class for threading.
    This must be done to not freeze up the main UI thread. This is because it is
    a networking task.
     */
    @Override
    protected String doInBackground(Object... params) {
        String jsonResponse = "";
        try {
            /*
            To the strJsonBody variable this code is customizable and is used to
            target the users using segments in One Signal. It contains an API unique to this app needed
            to make the API call. It will use the pass in variables to pass data in the notification and to
            which segment.
             */
            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic OTg5YzczODktMWM2Ny00ODkxLTk1ZDUtZmM3YTRkYzcwNzll");
            con.setRequestMethod("POST");
            String strJsonBody = "{"
                    +   "\"app_id\": \"2b2f27d6-facb-4c36-b5d9-d12e33244e02\","
                    +   "\"included_segments\": [\""+segCompany+"\"],"
                    +   "\"data\": {\"tag\": \""+segCompany +"\"},"
                    +   "\"contents\": {\"en\": \""+msg+ "from "+username+"\"}"
                    + "}";

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);
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