package com.example.craig.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.MyNotifiy;
import com.example.craig.finalyearproject.model.PlaceInformation;
import com.example.craig.finalyearproject.model.UsernameInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class is for the Recycler view needed for each
 * row of data added to a listview. It is an adapter class
 * it implements a view holder pattern which stops boolean
 * operations from being cancelled.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //Declare variables needed for the class
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=";
    private static String PHOTO_REF = "&maxheight=200&maxwidth=200&key=AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private ArrayList list;
    private Context context;
    private MapsActivity MyMap;
    private PlaceInformation info;
    private DatabaseReference ref;
    private MyNotifiy myNotifiy;
    private String username="";

    /**
     * This is a constructor which takes in arguments and sets
     * them to instance variables.
     * @param list
     * @param context
     * @param myMap
     */
    public RecyclerAdapter(ArrayList list,Context context, MapsActivity myMap) {
        this.list = list;
        this.context = context;
        MyMap = myMap;
    }

    /**
     * This is an implemented method which must be present for
     * the recycler view to function. It gets reference to the
     * parent component and gets a reference to the Firebase Database.
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row,parent,false);
        ref = FirebaseDatabase.getInstance().getReference("Notifications");
        return new ViewHolder(view);
    }

    /**
     * This is an implemented method which must be present for
     * the recycler view to function. This gets a reference to the
     * widgets contained for each row. It will then bind these to
     * rows in the listview. It will attach the listners needed also.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Get a reference to widgets and populate them
        info = (PlaceInformation) list.get(position);
        String url = BASE_URL + info.getPhoto()+ PHOTO_REF;
        Picasso.with(context).load(url).into(holder.image);
        holder.placeInfo.setText(""+info);

        /**
         * Attach listener to handle user input. Gets draws
         * the path between user device and destination.
         */
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMap.getPlaceOnMap(holder.getAdapterPosition());
            }
        });

        /**
         * Attach listener to handle user input. This allows
         * the user to navigate to the message page for the
         * current astro pitch.
         */
        holder.msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 Assign the object in the current row to a model class
                 which is used to access its fields. Assign values to
                 loacl variables through the maps class and astro pitch name.
                 Create the intenet used to encapsulate the variables created and
                 pass to a new activity of the message page.
                 */
                info = (PlaceInformation) list.get(holder.getAdapterPosition());
                String username = MyMap.getUserName();
                String companyName = info.getCompanyName();
                Intent intent = new Intent(context,MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Username",username);
                intent.putExtra("CompanyName",companyName);
                context.startActivity(intent);
            }
        });
        /**
         * Assign the value of true/false to the notifications
         * option depending on the account logged in.
         */
        holder.mySwitch.setChecked(info.isChecked());
        /**
         * Attach listener to handle user input. This is used for the
         * notification option on each row. It will use the One Signal
         * tags to assign one or delete one depending on the input of
         * true/false.
         */
        holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * Get the username from a static method from the map class.
                 * Assign the information from the row to a model class to
                 * allow for interaction and extraction of data. Create a model class
                 * for the notification option. Set the variables from the model class
                 * values for the row.
                 */
                username = MyMap.getUserName();
                myNotifiy = new MyNotifiy();
                info = (PlaceInformation) list.get(holder.getAdapterPosition());
                info.setChecked(isChecked);
                myNotifiy.setSignUp(isChecked);
                myNotifiy.setCompanyName(info.getCompanyName());
                myNotifiy.isSignUp();
                ref.child(username).child(info.getCompanyName()).setValue(myNotifiy);
                /**
                 * This if state is used to set the tag for
                 * One Signal or delete the tag. This is whether
                 * the notification slider is true/false
                 */
                if(isChecked){
                    OneSignal.sendTag(info.getCompanyName(),"1");
                }else {
                    OneSignal.deleteTag(info.getCompanyName());
                }

            }
        });
    }

    /**
     * Implemented method used to get the size of the
     * list.
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * This is an inner class which is used to implement the
     * view holder pattern used in the recycler view. It
     * will get a reference to the widgets for each row element
     * which are then interacted with by the view holder pattern.
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        /**
         * This is the declaration of the widgets for the
         * row.
         */
        ImageButton mapButton,msgButton;
        Switch mySwitch;
        ImageView image;
        TextView placeInfo;

        /**
         * Method used to get a reference to the components
         * and assign to the widget variables. Behaves like a
         * constructor for the view holder pattern.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            placeInfo =(TextView) itemView.findViewById(R.id.placeInfo);
            mapButton = (ImageButton) itemView.findViewById(R.id.imageButton1);
            msgButton = (ImageButton) itemView.findViewById(R.id.imageButton2);
            mySwitch = (Switch) itemView.findViewById(R.id.switch1);
        }

    }
}
