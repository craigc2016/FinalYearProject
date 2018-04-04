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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by craig on 29/03/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=";
    private static String PHOTO_REF = "&maxheight=200&maxwidth=200&key=AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private ArrayList list;
    private Context context;
    private MapsActivity MyMap;
    private PlaceInformation info;
    private DatabaseReference ref;
    private MyNotifiy myNotifiy;
    private static ArrayList<MyNotifiy> notifications;
    private String username="";
    public RecyclerAdapter(ArrayList list,Context context, MapsActivity myMap) {
        this.list = list;
        this.context = context;
        MyMap = myMap;
        notifications = new ArrayList<>();
        //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        //OneID = status.getSubscriptionStatus().getUserId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row,parent,false);
        ref = FirebaseDatabase.getInstance().getReference("Notifications");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        info = (PlaceInformation) list.get(holder.getAdapterPosition());
        Log.i("TESTING",""+info);
        String url = BASE_URL + info.getPhoto()+ PHOTO_REF;
        Picasso.with(context).load(url).into(holder.image);
        holder.placeInfo.append(""+info);
        Log.i("TAG",""+info);
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMap.getPlaceOnMap(position);
            }
        });

        holder.msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info = (PlaceInformation) list.get(holder.getAdapterPosition());
                String username = MyMap.getUserName();
                String companyName = info.getCompanyName();
                Toast.makeText(context,"MESSAGING FEATURE",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Username",username);
                intent.putExtra("CompanyName",companyName);

                context.startActivity(intent);
            }
        });

        holder.mySwitch.setChecked(info.isChecked());
        holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checked = checkingNotifications(isChecked,position);
                if(checked){
                    setUpOneSignal();
                }else {
                    OneSignal.deleteTag(info.getCompanyName());
                }

            }
        });
    }

    private boolean checkingNotifications(boolean isChecked, int position){
        username = MyMap.getUserName();
        myNotifiy = new MyNotifiy();
        //Log.i("MYPOS", "POS" + position);
        info = (PlaceInformation) list.get(position);
        info.setChecked(isChecked);
        myNotifiy.setSignUp(isChecked);
        myNotifiy.setCompanyName(info.getCompanyName());
        ref.child(username).child(info.getCompanyName()).setValue(myNotifiy);
        return myNotifiy.isSignUp();
    }
    private void setUpOneSignal(){
        OneSignal.sendTag(info.getCompanyName(),"1");
        //OneSignal.setEmail(UserID.getEmail().toLowerCase());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton mapButton,msgButton;
        Switch mySwitch;
        ImageView image;
        TextView placeInfo;


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
