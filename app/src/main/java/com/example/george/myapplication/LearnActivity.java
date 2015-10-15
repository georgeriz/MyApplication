package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    LinearLayout resultBox;
    boolean showTranslationFirst;
    Term term;
    ArrayList<Term> termsList = new ArrayList<>();
    final static String CORRECT_TAG = "if_it_was_correct";
    final static String WORD_CHECKED_TAG = "if_it_was_checked";
    final static String STATE_TERM = "term";
    final static String STATE_TERMS_LIST = "terms list";
    final static int CORRECT_COLOR = Color.GREEN;
    final static int WRONG_COLOR = Color.RED;
    final static int NEUTRAL_COLOR = Color.parseColor("#3399FF");
    private boolean updatePrevious;
    InputMethodManager imm;
    private boolean wasWordChecked;
    private boolean wasCorrect;

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
        resultBox = (LinearLayout) findViewById(R.id.learn_background);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectNextWord())
                    displayNextWord();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessInput = guessedWordEditText.getText().toString().trim();
                if (!guessInput.isEmpty()) {
                    boolean isCorrect = showTranslationFirst ? term.checkWord(guessInput) :
                            term.checkTranslation(guessInput);
                    wordChecked(isCorrect);
                    term.updateDegree(isCorrect);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.updateDegree(term.getID(), term.getDegree());
                    updatePrevious = true;
                    setResult(RESULT_OK);
                    termsList.remove(term);
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

        //other initializations
        wasCorrect = false;
        wasWordChecked = false;
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (savedInstanceState != null) {
            term = savedInstanceState.getParcelable(STATE_TERM);
            termsList = savedInstanceState.getParcelableArrayList(STATE_TERMS_LIST);
            updatePrevious = savedInstanceState.getBoolean(MainActivity.UPDATE_PREVIOUS);
            if (updatePrevious) {
                setResult(RESULT_OK);
            }
            wasWordChecked = savedInstanceState.getBoolean(WORD_CHECKED_TAG);
            wasCorrect = savedInstanceState.getBoolean(CORRECT_TAG);
            if (wasWordChecked) {
                wordChecked(wasCorrect);
            } else {
                displayNextWord();
            }
        } else {
            updatePrevious = false;
            if (selectNextWord())
                displayNextWord();
        }
    }

    private boolean selectNextWord() {
        if (termsList.size() == 0) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            Term[] terms = dbHelper.getListWithUnlearned(list_name);
            if (terms == null) {
                Toast.makeText(getApplicationContext(), "All words learned. Reset.", Toast.LENGTH_LONG)
                        .show();
                finish();
                return false;
            }
            termsList.addAll(Arrays.asList(terms));
        }
        //select a word to display/test
        term = termsList.get(new Random().nextInt(termsList.size()));
        return true;
    }

    private void displayNextWord() {
        if (showTranslationFirst) {
            shownWordText.setText(term.getTranslation());
        } else {
            shownWordText.setText(term.getWord());
        }
        guessedWordEditText.setText("");
        guessedWordEditText.requestFocus();
        imm.showSoftInput(guessedWordEditText, InputMethodManager.SHOW_IMPLICIT);
        resultText.setTextColor(Color.BLACK);
        resultText.setText("Result...");
        correctWordText.setText("");
        correctWordText.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        resultBox.setBackgroundColor(NEUTRAL_COLOR);
        wasWordChecked = false;
        nextButton.setClickable(false);
        checkButton.setClickable(true);
    }

    private void wordChecked(Boolean result) {
        if (result) {
            resultText.setText("Correct! :D");
            resultBox.setBackgroundColor(CORRECT_COLOR);
        } else {
            resultText.setText("Wrong :(");
            resultBox.setBackgroundColor(WRONG_COLOR);
        }
        correctWordText.setText(term.getWord() + " = " + term.getTranslation());
        correctWordText.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        nextButton.setClickable(true);
        checkButton.setClickable(false);
        wasWordChecked = true;
        wasCorrect = result;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(STATE_TERM, term);
        state.putParcelableArrayList(STATE_TERMS_LIST, termsList);
        state.putBoolean(MainActivity.UPDATE_PREVIOUS, updatePrevious);
        state.putBoolean(WORD_CHECKED_TAG, wasWordChecked);
        state.putBoolean(CORRECT_TAG, wasCorrect);
    }
}
