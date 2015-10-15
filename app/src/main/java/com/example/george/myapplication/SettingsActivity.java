package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    static final String SHARED_PREFERENCES = "com.example.george.myapplication.SHARED_PREFERENCES";

    static final String SHOW_TRANSLATION_SETTINGS = "show_translation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch showTranslationSwitch = (Switch) findViewById(R.id.show_translation_switch);
        Button resetButton = (Button) findViewById(R.id.resetLearningProcessButton);

        //get preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
        boolean showTranslation = settings.getBoolean(SHOW_TRANSLATION_SETTINGS, false);
        showTranslationSwitch.setChecked(showTranslation);
        showTranslationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putBoolean(SHOW_TRANSLATION_SETTINGS, isChecked);
                settingsEditor.apply();
            }
        });

        Intent intent = getIntent();
        final String list_name = intent.getStringExtra(BasicFunctions.LIST_NAME);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_name!=null) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.resetLearningProcess(list_name);
                    setResult(RESULT_OK);
                } else
                    Toast.makeText(getApplicationContext(), "Select a list first", Toast.LENGTH_LONG)
                            .show();
            }
        });
    }
}
