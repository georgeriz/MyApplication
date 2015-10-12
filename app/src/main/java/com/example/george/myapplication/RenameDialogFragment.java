package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class RenameDialogFragment extends DialogFragment {
    String list_name;
    EditText listNameEditText;

    public static RenameDialogFragment newInstance(String list_name) {
        RenameDialogFragment f = new RenameDialogFragment();
        Bundle args = new Bundle();
        args.putString("list_name", list_name);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState==null){
            list_name = getArguments().getString("list_name");
        }else{
            list_name = savedInstanceState.getString("list_name");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        listNameEditText = new EditText(getActivity());
        listNameEditText.setText(list_name);
        builder.setView(listNameEditText);
        builder.setMessage("Edit name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String new_list_name = listNameEditText.getText().toString().trim();
                        if(BasicFunctions.renameFromToLanguageCheck(getActivity(),
                                list_name, new_list_name)){
                            ((ListActivity) getActivity()).onDialogPositiveClick(new_list_name);
                        }
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

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("list_name", listNameEditText.getText().toString());
    }
}
