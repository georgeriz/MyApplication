package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LearnActivity extends AppCompatActivity {
    TextView shownWordText;
    EditText guessedWordEditText;
    TextView resultText;
    Button nextButton;
    Button checkButton;
    Button editButton;
    String list_name;
    TextView correctWordText;
    boolean showTranslationFirst;
    Term term;
    ArrayList<Term> termsList = new ArrayList<>();
    final static String STATE_TERM = "term";
    final static String STATE_TERMS_LIST = "terms list";
    final static int CORRECT_COLOR = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        shownWordText = (TextView) findViewById(R.id.shown_word);
        guessedWordEditText = (EditText) findViewById(R.id.guessed_word);
        resultText = (TextView) findViewById(R.id.result);
        nextButton = (Button) findViewById(R.id.nextButton);
        editButton = (Button) findViewById(R.id.edit_in_learn);
        checkButton = (Button) findViewById(R.id.checkButton);
        correctWordText = (TextView) findViewById(R.id.correct_word);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNextWord();
                displayNextWord();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessInput = guessedWordEditText.getText().toString().trim();
                if (!guessInput.isEmpty()) {
                    boolean isCorrect = showTranslationFirst ? term.checkWord(guessInput) : term.checkTranslation(guessInput);
                    if (isCorrect) {
                        resultText.setText("Correct! :D");
                        resultText.setTextColor(CORRECT_COLOR);
                    } else {
                        resultText.setText("Wrong :(");
                        resultText.setTextColor(Color.RED);
                    }
                    term.updateDegree(isCorrect);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.updateDegree(term.getID(), term.getDegree());
                    setResult(RESULT_OK);
                    termsList.remove(term);
                    correctWordText.setText(showTranslationFirst ? term.getWord() : term.getTranslation());
                    correctWordText.setVisibility(View.VISIBLE);
                    editButton.setVisibility(View.VISIBLE);
                    nextButton.setClickable(true);
                    checkButton.setClickable(false);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicFunctions.openActivityForResultWithTerm(LearnActivity.this, EditActivity.class, term);
                selectNextWord();
                displayNextWord();
            }
        });

        Intent intent = getIntent();
        list_name = intent.getStringExtra(BasicFunctions.LIST_NAME);
        setTitle(list_name);

        SharedPreferences settings = getSharedPreferences(SettingsActivity.SHARED_PREFERENCES, 0);
        showTranslationFirst = settings.getBoolean(SettingsActivity.SHOW_TRANSLATION_SETTINGS, false);



        if(savedInstanceState!=null) {
            term = savedInstanceState.getParcelable(STATE_TERM);
            termsList = savedInstanceState.getParcelableArrayList(STATE_TERMS_LIST);
        } else {
            selectNextWord();
        }
        displayNextWord();
    }

    private void selectNextWord() {
        if (termsList.size() == 0) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            Term[] terms = dbHelper.getList(list_name);
            if (terms == null) {
                //some error control is needed here
                return;
            }
            termsList.addAll(Arrays.asList(terms));
        }
        //select a word to display/test
        term = termsList.get(new Random().nextInt(termsList.size()));
    }

    private void displayNextWord(){
        if(showTranslationFirst) {
            shownWordText.setText(term.getTranslation());
        } else {
            shownWordText.setText(term.getWord());
        }
        guessedWordEditText.setText("");
        resultText.setTextColor(Color.BLACK);
        resultText.setText("Result...");
        correctWordText.setText("");
        correctWordText.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);

        nextButton.setClickable(false);
        checkButton.setClickable(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(STATE_TERM, term);
        state.putParcelableArrayList(STATE_TERMS_LIST, termsList);
    }
}
