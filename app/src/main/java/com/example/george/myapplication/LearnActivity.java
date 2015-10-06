package com.example.george.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LearnActivity extends AppCompatActivity {
    TextView shownWordText;
    EditText guessedWordEditText;
    TextView resultText;
    Button nextButton;
    Button checkButton;
    String list_name;
    DBHelper dbHelper;
    TextView correctWordText;
    boolean showTranslation;
    final static int CORRECT_COLOR = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        shownWordText = (TextView) findViewById(R.id.shown_word);
        guessedWordEditText = (EditText) findViewById(R.id.guessed_word);
        resultText = (TextView) findViewById(R.id.result);
        nextButton = (Button) findViewById(R.id.nextButton);
        checkButton = (Button) findViewById(R.id.checkButton);
        correctWordText = (TextView) findViewById(R.id.correct_word);

        Intent intent = getIntent();
        list_name = intent.getStringExtra(MainActivity.LIST_NAME);
        setTitle(list_name);

        SharedPreferences settings = getSharedPreferences(ListActivity.SHARED_PREFERENCES, 0);
        showTranslation = settings.getBoolean(ListActivity.SHOW_TRANSLATION_SETTINGS, false);

        dbHelper = new DBHelper(getApplicationContext());
        Term[] terms = dbHelper.getList(list_name);
        if(terms==null) {
            //some error control is needed here
            return;
        }

        ArrayList<Term> myList = new ArrayList<Term>(Arrays.asList(terms));
        testNewWord(myList);
    }

    private void testNewWord(final ArrayList<Term> myList) {
        if(myList.size() == 0) {
            Term[] terms = dbHelper.getList(list_name);
            if(terms==null) {
                //some error control is needed here
                return;
            }

            for(Term term: terms) {
                myList.add(term);
            }
        }

        //select a word to display/test
        final Term myTerm = myList.get(new Random().nextInt(myList.size()));

        //prepare the UI
        if(showTranslation) {
            shownWordText.setText(myTerm.getTranslation());
        } else {
            shownWordText.setText(myTerm.getWord());
        }
        guessedWordEditText.setText("");
        resultText.setTextColor(Color.BLACK);
        resultText.setText("Result...");
        correctWordText.setText("");
        correctWordText.setVisibility(View.INVISIBLE);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNewWord(myList);
            }
        });
        nextButton.setClickable(false);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessInput = guessedWordEditText.getText().toString().trim().toLowerCase();
                if (!guessInput.isEmpty()) {
                    boolean isCorrect = showTranslation ? myTerm.checkWord(guessInput) : myTerm.checkTranslation(guessInput);
                    if (isCorrect) {
                        resultText.setText("Correct! :D");
                        resultText.setTextColor(CORRECT_COLOR);
                    } else {
                        resultText.setText("Wrong :(");
                        resultText.setTextColor(Color.RED);
                    }
                    myTerm.updateDegree(isCorrect);
                    dbHelper.updateDegree(myTerm.getID(), myTerm.getDegree());
                    myList.remove(myTerm);
                    correctWordText.setText(showTranslation ? myTerm.getWord() : myTerm.getTranslation());
                    correctWordText.setVisibility(View.VISIBLE);
                    nextButton.setClickable(true);
                    checkButton.setClickable(false);
                }
            }
        });
        checkButton.setClickable(true);
    }
}
