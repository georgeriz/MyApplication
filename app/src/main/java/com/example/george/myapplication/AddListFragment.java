package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by George on 2015-11-10.
 */
public class AddListFragment extends DialogFragment {

    public static AddListFragment newInstance(String[] languages) {
        AddListFragment fragment = new AddListFragment();
        Bundle args = new Bundle();
        args.putStringArray("languages", languages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] languages = getArguments().getStringArray("languages");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText listNameEditText = new EditText(getActivity());
        builder.setView(listNameEditText);
        builder.setMessage("Edit name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String new_list_name = listNameEditText.getText().toString().trim();
                        for(String list_name: languages){
                            if(list_name.equals(new_list_name)) {
                                Toast.makeText(getActivity(), "This already exists", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }
                        //TODO Create new list
                        //TODO update local data
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
