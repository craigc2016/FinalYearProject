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

import com.example.craig.finalyearproject.model.PlaceInformation;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by craig on 29/03/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?photoreference=";
    private static String PHOTO_REF = "&maxheight=200&maxwidth=200&key=AIzaSyAQU76H2D4U1xehhVGJqTUDTHhFO6ImEIs";
    private ArrayList list;

    public RecyclerAdapter(ArrayList list, Context context, MapsActivity myMap) {
        this.list = list;
        this.context = context;
        MyMap = myMap;
    }

    private Context context;
    private MapsActivity MyMap;
    private PlaceInformation info;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        info = (PlaceInformation) list.get(position);
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

        holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("MYPOS", "POS" + position);
            }
        });
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
