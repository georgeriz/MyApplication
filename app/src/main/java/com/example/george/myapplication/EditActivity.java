package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class EditActivity extends AppCompatActivity {
    Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        term = (Term) intent.getParcelableExtra(ListActivity.EXTRA_NAME_TERM);


        final EditText wordUpdate = (EditText) findViewById(R.id.word_update);
        final EditText translationUpdate = (EditText) findViewById(R.id.translation_update);

        wordUpdate.setText(term.getWord());
        translationUpdate.setText(term.getTranslation());

        final CheckBox resetDegreeCheckBox = (CheckBox) findViewById(R.id.resetDegreeCheckBox);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myWord = wordUpdate.getText().toString().trim().toLowerCase();
                String myTranslation = translationUpdate.getText().toString().trim().toLowerCase();
                boolean resetDegree = resetDegreeCheckBox.isChecked();
                if(!(myWord.equals("") || myTranslation.equals(""))) {
                    int myDegree = resetDegree? 0: term.getDegree();
                    term.setWord(myWord);
                    term.setTranslation(myTranslation);
                    term.setDegree(myDegree);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    dbHelper.editWord(term);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.delete_word) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.deleteWord(term.getID());
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
