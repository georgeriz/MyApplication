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
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText listNameEditText = new EditText(getActivity());
        builder.setView(listNameEditText);
        builder.setMessage("Edit name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String new_list_name = listNameEditText.getText().toString().trim();
                        for(String list_name: ((MainActivity)getActivity()).list_names){
                            if(list_name.equals(new_list_name)) {
                                Toast.makeText(getActivity(), "This already exists", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }
                        //Create new list
                        BasicFunctions.addList(getActivity(), new_list_name);
                        //update local data
                        ((MainActivity) getActivity()).doAddListSuccessful(new_list_name);
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
