package com.example.george.myapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    static final String SHARED_PREFERENCES = "com.example.george.myapplication.SHARED_PREFERENCES";

    static final String SHOW_TRANSLATION_SETTINGS = "show_translation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch showTranslationSwitch = (Switch) findViewById(R.id.show_translation_switch);



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
    }
}
