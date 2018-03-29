package com.example.craig.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craig.finalyearproject.model.PlaceInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by craig on 03/03/2018.
 */

public class MyCustomAdapter extends ArrayAdapter {
    private ArrayList list;
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=";
    private static String PHOTO_REF = "&maxheight=200&maxwidth=200&key=AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private static final String IMAGE_NOT_FOUND = "https://gaygeekgab.files.wordpress.com/2015/05/wpid-photo-317.png";
    private Context context;
    private MapsActivity MyMap;
    private PlaceInformation info;
    public MyCustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList list,MapsActivity activity) {
        super(context, R.layout.custom_row, list);
        this.list = list;
        this.context = context;
        this.MyMap = activity;
    }

    public View getView(final int position, View convertView, final ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.custom_row,parent,false);
        ImageButton mapButton = (ImageButton)  convertView .findViewById(R.id.imageButton1);
        ImageButton msgButton = (ImageButton)  convertView .findViewById(R.id.imageButton2);
        Switch mySwitch = (Switch)  convertView.findViewById(R.id.switch1);
        ImageView image = (ImageView)  convertView.findViewById(R.id.image);
        info = (PlaceInformation) list.get(position);
        String url = BASE_URL + info.getPhoto()+ PHOTO_REF;
        Picasso.with(getContext()).load(url).into(image);
        //TextView placeInfo = (TextView)   convertView.findViewById(R.id.placeInfo);
        //placeInfo.append(""+info);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MYERROR","" + position);
                MyMap.getPlaceOnMap(position);
                //Toast.makeText(getContext(),""+info.getPosition(),Toast.LENGTH_LONG).show();
            }
        });

        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = MyMap.getUserName();
                String companyName = info.getCompanyName();
                Toast.makeText(getContext(),"MESSAGING FEATURE",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Username",username);
                intent.putExtra("CompanyName",companyName);
                getContext().startActivity(intent);
            }
        });

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        return  convertView ;
    }
}