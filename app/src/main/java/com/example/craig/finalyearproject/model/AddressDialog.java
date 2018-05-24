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
 * This class is used for the address dialog input box.
 * This will allow for the custom creation of a dialog
 * box. It will create and get a reference to the input
 * widgets of edit text fields.
 */

public class AddressDialog extends AppCompatDialogFragment {
    private EditText addLat,addLon,addCode;
    private AddressDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        /**
         * Create an instance of alert dialog using the builder class
         * Inflate that view similar to recycler view creation of
         * rows.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        /**
         * get references to the edit text widgets
         */
        addLat = (EditText) view.findViewById(R.id.editLat);
        addLon = (EditText) view.findViewById(R.id.editLon);
        addCode = (EditText) view.findViewById(R.id.editCode);
        /**
         * Set the dialogs view for the positive negative buttons.
         * The cancel button just closes the dialog box. The positive
         * will get the text from the edit text widgets. This is then passed
         * to the listener class.
         */
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
                        /**
                         * Pass the values to the listener class
                         */
                        try{
                            listener.getTexts(Double.parseDouble(lat),Double.parseDouble(lon),code);
                        }catch (Exception e){
                            Toast.makeText(getContext(),"ERROR",Toast.LENGTH_LONG).show();

                        }
                    }
                });
        /**
         * Return the builder that is created
         */
        return builder.create();
    }

    /**
     * This is an implemented method which is used to attach the interface
     * class which will act as the link between the dialog box and the activity class.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            /**
             * Cast the listener to the custom interface class
             */
            listener = (AddressDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement AddressDialogListener");
        }
    }

    /**
     * Inner interface class used to pass the values from
     * the dialog box to the underlying activity class.
     */
    public interface AddressDialogListener {
        void getTexts(double lat,double lon,String code );
    }
}
