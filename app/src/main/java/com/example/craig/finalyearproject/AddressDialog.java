package com.example.craig.finalyearproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by craig on 08/12/2017.
 */

public class AddressDialog extends AppCompatDialogFragment {
    private EditText addText;
    private AddressDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        addText = (EditText) view.findViewById(R.id.editAddress);
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
                        String address = addText.getText().toString();
                        listener.getTexts(address);
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
        void getTexts(String address);
    }
}
