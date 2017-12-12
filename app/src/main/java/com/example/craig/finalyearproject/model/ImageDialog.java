package com.example.craig.finalyearproject.model;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.craig.finalyearproject.R;

/**
 * Created by craig on 12/12/2017.
 */

public class ImageDialog extends AppCompatDialogFragment {
    private ImageDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);

        builder.setView(view)
                .setTitle("Set Image as Profile Picture")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String response = "No";
                        listener.getTexts(response);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String response = "Yes";
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                        listener.getTexts(response);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ImageDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement AddressDialogListener");
        }
    }

    public interface ImageDialogListener {
        void getTexts(String response);
    }
}
