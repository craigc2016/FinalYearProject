package com.example.craig.finalyearproject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private Context context;
    private MapsActivity MyMap;
    private PlaceInformation info;
    private ImageButton imageButton;
    public MyCustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList list,MapsActivity activity) {
        super(context, R.layout.custom_row, list);
        this.list = list;
        this.context = context;
        this.MyMap = activity;
    }

    public View getView(final int position, final View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row,parent,false);
        imageButton = (ImageButton) customView.findViewById(R.id.image1);
        ImageView image = (ImageView) customView.findViewById(R.id.image);
        info = (PlaceInformation) list.get(position);
        String url = BASE_URL + info.getPhoto()+ PHOTO_REF;
        Picasso.with(getContext()).load(url).into(image);
        TextView placeInfo = (TextView)  customView.findViewById(R.id.placeInfo);
        placeInfo.append(""+info);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MYERROR","" + position);
                MyMap.getPlaceOnMap(position);
                //Toast.makeText(getContext(),""+info.getPosition(),Toast.LENGTH_LONG).show();
            }
        });
        return customView;
    }
}
