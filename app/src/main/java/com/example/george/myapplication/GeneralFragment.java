package com.example.george.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class GeneralFragment extends Fragment {
    TextView progressPercentage;
    ProgressBar listProgressBar;
    ListActivity listActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        //buttons, switches
        Button learnButton = (Button) rootView.findViewById(R.id.learnButton);
        Button addButton = (Button) rootView.findViewById(R.id.addButton);
        Switch showTranslationSwitch = (Switch) rootView.findViewById(R.id.show_translation_switch);

        listActivity = (ListActivity) getActivity();
        final String list_name = listActivity.getList_name();
        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity_intent(LearnActivity.class, list_name);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity_intent(AddActivity.class, list_name);
            }
        });

        //get preferences
        SharedPreferences settings = getActivity().getSharedPreferences(ListActivity.SHARED_PREFERENCES, 0);
        boolean showTranslation = settings.getBoolean(ListActivity.SHOW_TRANSLATION_SETTINGS, false);
        showTranslationSwitch.setChecked(showTranslation);
        showTranslationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getActivity().getSharedPreferences(ListActivity.SHARED_PREFERENCES, 0);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putBoolean(ListActivity.SHOW_TRANSLATION_SETTINGS, isChecked);
                settingsEditor.apply();
            }
        });

        //other
        progressPercentage = (TextView) rootView.findViewById(R.id.progress_percentage);
        listProgressBar = (ProgressBar) rootView.findViewById(R.id.listProgressBar);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        updateProgress(listActivity.getSize(), listActivity.getProgress());
    }

    public void updateProgress(int max, int progress) {
        //learning progress
        listProgressBar.setMax(max);
        listProgressBar.setProgress(progress);

        //total words percentage
        String progressPercentageText = progress + "/" + max;
        progressPercentage.setText(progressPercentageText);
    }

    private void open_activity_intent(Class<?> cls, String list_name) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(MainActivity.LIST_NAME, list_name);
        startActivityForResult(intent, ListActivity.UPDATE_LIST_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ListActivity.UPDATE_LIST_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            listActivity.updateTerms();
        }
    }
}
