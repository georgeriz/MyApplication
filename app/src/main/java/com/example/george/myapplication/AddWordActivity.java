package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.george.myapplication.data.DAO;
import com.example.george.myapplication.data.Term;

import java.util.ArrayList;

public class AddWordActivity extends AppCompatActivity {
    public static final int ADD_TERMS_CODE = 1;
    public static final String EXTRA_ADD_TERMS = "add_terms";
    EditText wordInput;
    EditText translationInput;
    Button saveButton;
    String list_name;
    InputMethodManager imm;
    Term[] terms;
    ArrayList<Term> newTerms = new ArrayList<>();
    DAO dbVan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        wordInput = (EditText) findViewById(R.id.word_input);
        translationInput = (EditText) findViewById(R.id.translation_input);
        saveButton = (Button)  findViewById(R.id.save_button);

        Intent intent = getIntent();
        list_name = intent.getStringExtra(ManageListActivity.LIST_NAME);

        dbVan = new DAO(getApplicationContext());
        terms = dbVan.getList(list_name);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordInput.getText().toString().trim();
                String translation = translationInput.getText().toString().trim();
                if (!(word.equals("") || translation.equals(""))) {
                    if (terms != null) {
                        for (Term term : terms) {
                            if (term.getWord().equals(word)) {
                                Toast.makeText(getApplicationContext(), "This word already exists, " +
                                        "so it cannot be added. Try editing.", Toast.LENGTH_LONG).show();
                                saveNewWord();
                                return;
                            }
                        }
                    }
                    int id = dbVan.addWord(word, translation, list_name);
                    Term new_term = new Term(id, word, translation, 0);
                    newTerms.add(new_term);
                    Intent result_intent = new Intent();
                    result_intent.putExtra(EXTRA_ADD_TERMS, newTerms);
                    setResult(RESULT_OK, result_intent);
                    saveNewWord();
                }
            }
        });

        saveNewWord();
    }

    private void saveNewWord() {
        wordInput.setText("");
        wordInput.requestFocus();
        imm.showSoftInput(wordInput, InputMethodManager.SHOW_IMPLICIT);
        translationInput.setText("");
    }
}
