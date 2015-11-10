package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by George on 2015-11-10.
 */
public class MergeDialogFragment extends DialogFragment {
    public static MergeDialogFragment newInstance(String[] languages) {
        MergeDialogFragment fragment = new MergeDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray("languages", languages);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] languages = getArguments().getStringArray("languages");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_main_merge, null);
        builder.setView(dialogView);
        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);
        final Spinner spinner2 = (Spinner) dialogView.findViewById(R.id.spinner2);
        final ArrayAdapter<String> list_names_array_adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, languages);
        spinner1.setAdapter(list_names_array_adapter);
        spinner2.setAdapter(list_names_array_adapter);
        builder.setTitle("Select two lists");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String language_from = spinner1.getSelectedItem().toString();
                String language_to = spinner2.getSelectedItem().toString();
                if (language_from.equals(language_to)) {
                    Toast.makeText(getActivity(), "Select two different lists", Toast.LENGTH_LONG)
                            .show();
                } else {
                    //update persistent data (database)
                    BasicFunctions.mergeFromToLanguageSafe(getActivity(), language_from, language_to);
                    //update local data (UI)
                    ((MainActivity) getActivity()).doMergeSuccessful(language_from);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        return builder.create();
    }
}
