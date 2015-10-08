package com.example.george.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.util.List;


public class GeneralFragment extends Fragment {
    DBHelper dbHelper;
    TextView progressPercentage;
    ProgressBar listProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        //buttons, switches
        Button learnButton = (Button) rootView.findViewById(R.id.learnButton);
        Button addButton = (Button) rootView.findViewById(R.id.addButton);
        Switch showTranslationSwitch = (Switch) rootView.findViewById(R.id.show_translation_switch);

        //for debugging
        ListActivity listActivity = (ListActivity) getActivity();
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
                settingsEditor.commit();
            }
        });

        //other
        dbHelper = new DBHelper(getActivity());
        progressPercentage = (TextView) rootView.findViewById(R.id.progress_percentage);
        listProgressBar = (ProgressBar) rootView.findViewById(R.id.listProgressBar);

        updateProgress(list_name);

        return rootView;
    }

    private void updateProgress(String list_name) {
        Term[] terms = dbHelper.getList(list_name);

        //error control
        if(terms==null){return;}

        //learning progress
        listProgressBar.setMax(terms.length);
        int progress_count = 0;
        for(Term term: terms) {
            if(term.getDegree() == 1000) {
                progress_count++;
            }
        }
        listProgressBar.setProgress(progress_count);

        //total words percentage
        String progressPercentageText = progress_count + "/" + terms.length;
        progressPercentage.setText(progressPercentageText);
    }

    private void open_activity_intent(Class<?> cls, String list_name) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(MainActivity.LIST_NAME, list_name);
        startActivity(intent);
    }

}
