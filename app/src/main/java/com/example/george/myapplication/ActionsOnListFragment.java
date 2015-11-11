package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ActionsOnListFragment extends DialogFragment {

    public static ActionsOnListFragment newInstance(String list_name) {
        ActionsOnListFragment f = new ActionsOnListFragment();
        Bundle args = new Bundle();
        args.putString("list_name", list_name);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String list_name = getArguments().getString("list_name");

        String[] actions = new String[] {"Rename", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an action");
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        RenameDialogFragment renameDialogFragment = RenameDialogFragment.newInstance(list_name);
                        renameDialogFragment.show(getFragmentManager(), "rename");
                        break;
                    case 1:
                        BasicFunctions.deleteList(getActivity(), list_name);
                        ((MainActivity)getActivity()).doDeleteSuccessful(list_name);
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }
}
