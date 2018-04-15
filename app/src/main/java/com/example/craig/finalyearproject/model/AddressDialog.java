package com.example.craig.finalyearproject.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.craig.finalyearproject.MapsActivity;
import com.example.craig.finalyearproject.R;

/**
 * Created by craig on 08/12/2017.
 */


public class AddressDialog extends AppCompatDialogFragment {
    private EditText addLat,addLon,addCode;
    private AddressDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        addLat = (EditText) view.findViewById(R.id.editLat);
        addLon = (EditText) view.findViewById(R.id.editLon);
        addCode = (EditText) view.findViewById(R.id.editCode);
        builder.setView(view)
                .setTitle("Add Address")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String lat = addLat.getText().toString();
                        String lon = addLon.getText().toString();
                        String code = addCode.getText().toString();
                        try{
                            listener.getTexts(Double.parseDouble(lat),Double.parseDouble(lon),code);
                        }catch (Exception e){
                            Toast.makeText(getContext(),"ERROR",Toast.LENGTH_LONG).show();

                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddressDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement AddressDialogListener");
        }
    }

    public interface AddressDialogListener {
        void getTexts(double lat,double lon,String code );
    }
}
