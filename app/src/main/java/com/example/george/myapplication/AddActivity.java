package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {
    EditText wordInput;
    EditText translationInput;
    EditText languageInput;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        wordInput = (EditText) findViewById(R.id.word_input);
        translationInput = (EditText) findViewById(R.id.translation_input);
        languageInput = (EditText) findViewById(R.id.language_input);
        saveButton = (Button)  findViewById(R.id.save_button);

        Intent intent = getIntent();
        String list_name = intent.getStringExtra(MainActivity.LIST_NAME);

        saveNewWord(list_name);
    }

    private void saveNewWord(final String list_name) {
        wordInput.setText("");
        translationInput.setText("");
        languageInput.setText(list_name);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordInput.getText().toString().trim().toLowerCase();
                String translation = translationInput.getText().toString().trim().toLowerCase();
                String language = languageInput.getText().toString().trim();
                if(!(word.equals("") || translation.equals("") || language.equals(""))) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.insertWord(word, translation, language);
                    saveNewWord(language);
                }
            }
        });
    }
}
