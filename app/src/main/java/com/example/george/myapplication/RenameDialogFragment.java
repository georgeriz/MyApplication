package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.george.myapplication.data.BasicFunctions;

public class RenameDialogFragment extends DialogFragment {

    public static RenameDialogFragment newInstance(String list_name) {
        RenameDialogFragment f = new RenameDialogFragment();
        Bundle args = new Bundle();
        args.putString("list_name", list_name);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String list_name = getArguments().getString("list_name");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText listNameEditText = new EditText(getActivity());
        listNameEditText.setText(list_name);
        builder.setView(listNameEditText);
        builder.setMessage("Give a name for the new list")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String new_list_name = listNameEditText.getText().toString().trim();
                        for (String a_list : ((MainActivity) getActivity()).list_names) {
                            if (a_list.equals(new_list_name)) {
                                Toast.makeText(getActivity(), "This name already exists." +
                                        "Try merging the two lists instead.", Toast.LENGTH_LONG)
                                        .show();
                                return;
                            }
                        }
                        BasicFunctions.renameFromToLanguage(getActivity(), list_name, new_list_name);
                        ((MainActivity)getActivity()).doRenameSuccessful(list_name, new_list_name);
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
