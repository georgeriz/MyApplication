package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.george.myapplication.data.DAO;
import com.example.george.myapplication.data.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LearnActivity extends AppCompatActivity {
    final static String STATE_CORRECT = "if_it_was_correct";
    final static String STATE_WORD_CHECKED = "if_it_was_checked";
    final static String STATE_TERM = "term";
    final static String STATE_TERMS_LIST = "terms list";
    final static String STATE_ARTICLES = "articles";
    final static int LEARN_CODE = 3;
    final static int CORRECT_COLOR = Color.GREEN;
    final static int WRONG_COLOR = Color.RED;
    final static int NEUTRAL_COLOR = Color.parseColor("#3399FF");
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
    InputMethodManager imm;
    Term term;
    ArrayList<Term> termsList;
    private boolean wasWordChecked;
    private boolean wasCorrect;
    private String[] articles;
    DAO dbVan;

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


        Intent intent = getIntent();
        list_name = intent.getStringExtra(ListActivity.LIST_NAME);
        setTitle(list_name);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        showTranslationFirst = settings.getBoolean("show_translation", true);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setResult(RESULT_OK);
        dbVan = new DAO(getApplicationContext());

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectNextWord())
                    displayNextWordUI();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessInput = guessedWordEditText.getText().toString().trim();
                if (!guessInput.isEmpty()) {
                    guessInput = addArticle(guessInput, radioGroup.getCheckedRadioButtonId());
                    boolean isCorrect = showTranslationFirst ? term.checkWord(guessInput) :
                            term.checkTranslation(guessInput);
                    inputCheckUI(isCorrect);
                    term.updateDegree(isCorrect);
                    dbVan.updateDegree(term.getID(), term.getDegree());
                    termsList.remove(term);
                }
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult((new Intent(getApplicationContext(), EditActivity.class))
                        .putExtra(ListActivity.TERM, term), EditActivity.EDIT_TERM_CODE);
            }
        });

        if (savedInstanceState == null) {
            termsList = new ArrayList<>();
            articles = dbVan.getArticles(list_name);
            wasCorrect = false;
            wasWordChecked = false;
            if (selectNextWord())
                displayNextWordUI();
        } else {
            term = savedInstanceState.getParcelable(STATE_TERM);
            termsList = savedInstanceState.getParcelableArrayList(STATE_TERMS_LIST);
            articles = savedInstanceState.getStringArray(STATE_ARTICLES);
            wasWordChecked = savedInstanceState.getBoolean(STATE_WORD_CHECKED);
            wasCorrect = savedInstanceState.getBoolean(STATE_CORRECT);
            if (wasWordChecked) {
                inputCheckUI(wasCorrect);
            } else {
                displayNextWordUI();
            }
        }

        if (articles != null)
            for (String article : articles) {
                RadioButton radioButton = new RadioButton(getApplicationContext());
                radioButton.setText(article);
                radioButton.setTextColor(Color.BLACK);
                radioGroup.addView(radioButton);
            }
    }

    private String addArticle(String guessInput, int radio_id) {
        if (radio_id == -1)
            return guessInput;
        RadioButton rb = (RadioButton) radioGroup.findViewById(radio_id);
        return rb.getText() + " " + guessInput;
    }

    private boolean selectNextWord() {
        if (termsList.size() == 0) {
            Term[] terms = dbVan.getListWithUnlearned(list_name);
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

    private void displayNextWordUI() {
        radioGroup.setVisibility(View.GONE);
        if (showTranslationFirst) {
            shownWordText.setText(term.getTranslation());
            if (articles != null)
                for (String article : articles)
                    if (term.getWord().startsWith(article + " ")) {
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
        radioGroup.clearCheck();
    }

    private void inputCheckUI(Boolean result) {
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
        state.putBoolean(STATE_WORD_CHECKED, wasWordChecked);
        state.putBoolean(STATE_CORRECT, wasCorrect);
        state.putStringArray(STATE_ARTICLES, articles);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditActivity.EDIT_TERM_CODE && resultCode == RESULT_OK) {
            if (selectNextWord())
                displayNextWordUI();
        }
    }
}