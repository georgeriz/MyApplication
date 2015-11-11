package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    RadioGroup radioGroup;
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
    InputMethodManager imm;
    private boolean wasWordChecked;
    private boolean wasCorrect;
    private String[] articles;

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
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

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
                int radio_id = radioGroup.getCheckedRadioButtonId();
                if (!guessInput.isEmpty()) {
                    guessInput = constructUserInput(guessInput, radio_id);
                    boolean isCorrect = showTranslationFirst ? term.checkWord(guessInput) :
                            term.checkTranslation(guessInput);
                    wordChecked(isCorrect);
                    term.updateDegree(isCorrect);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.updateDegree(term.getID(), term.getDegree());
                    termsList.remove(term);
                    //setResult(RESULT_OK);
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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        showTranslationFirst = settings.getBoolean("show_translation", true);

        //other initializations
        wasCorrect = false;
        wasWordChecked = false;
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        articles = BasicFunctions.getArticles(this, list_name);

        if (savedInstanceState != null) {
            term = savedInstanceState.getParcelable(STATE_TERM);
            termsList = savedInstanceState.getParcelableArrayList(STATE_TERMS_LIST);

            wasWordChecked = savedInstanceState.getBoolean(WORD_CHECKED_TAG);
            wasCorrect = savedInstanceState.getBoolean(CORRECT_TAG);
            if (wasWordChecked) {
                wordChecked(wasCorrect);
            } else {
                displayNextWord();
            }
        } else {

            if (selectNextWord())
                displayNextWord();
        }
        setResult(RESULT_OK);
    }

    private String constructUserInput(String guessInput, int radio_id) {
        if(radio_id == -1)
            return guessInput;
        RadioButton rb = (RadioButton) radioGroup.findViewById(radio_id);
        return rb.getText() + " " + guessInput;
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
        radioGroup.setVisibility(View.GONE);
        if (showTranslationFirst) {
            shownWordText.setText(term.getTranslation());
            for(String article: articles)
                if(term.getWord().startsWith(article + " ")) {
                    Log.i(MainActivity.TAG, "starts with " + article);
                    radioGroup.setVisibility(View.VISIBLE);
                    break;
                }
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

        createRadioButtons();
        radioGroup.clearCheck();
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
        state.putBoolean(WORD_CHECKED_TAG, wasWordChecked);
        state.putBoolean(CORRECT_TAG, wasCorrect);
    }

    private void createRadioButtons() {
        int a = radioGroup.getChildCount();
        if (a == 0) {
            for (String article : articles) {
                RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setText(article);
                radioButton.setTextColor(Color.BLACK);
                radioGroup.addView(radioButton);
            }
        }
    }
}