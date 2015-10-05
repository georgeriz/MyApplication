package com.example.george.myapplication;

import android.content.Intent;
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
    TextView myWordText;
    EditText myTranslationEditText;
    TextView myResultText;
    Button nextButton;
    Button checkButton;
    String list_name;
    DBHelper dbHelper;

    final static int CORRECT_COLOR = Color.BLUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        myWordText = (TextView) findViewById(R.id.word);
        myTranslationEditText = (EditText) findViewById(R.id.translation);
        myResultText = (TextView) findViewById(R.id.result);
        nextButton = (Button) findViewById(R.id.nextButton);
        checkButton = (Button) findViewById(R.id.checkButton);

        Intent intent = getIntent();
        list_name = intent.getStringExtra(MainActivity.LIST_NAME);
        setTitle(list_name);

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
        myWordText.setText(myTerm.getWord());
        myTranslationEditText.setText("");
        myResultText.setTextColor(Color.BLACK);
        myResultText.setText("Result...");

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
                String myTranslation = myTranslationEditText.getText().toString().trim().toLowerCase();
                if(!myTranslation.isEmpty()) {
                    if (myTerm.checkTranslation(myTranslation)) {
                        myTerm.updateDegree(true);
                        myResultText.setText("Correct! :D");
                        myResultText.setTextColor(CORRECT_COLOR);
                    } else {
                        myTerm.updateDegree(false);
                        myResultText.setText("Wrong :(");
                        myResultText.setTextColor(Color.RED);
                    }
                    dbHelper.updateDegree(myTerm.getID(), myTerm.getDegree());
                    myList.remove(myTerm);
                    nextButton.setClickable(true);
                    checkButton.setClickable(false);
                }
            }
        });
        checkButton.setClickable(true);
    }
}
