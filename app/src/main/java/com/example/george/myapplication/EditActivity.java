package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.george.myapplication.data.DAO;
import com.example.george.myapplication.data.Term;

public class EditActivity extends AppCompatActivity {
    public static final int EDIT_TERM_CODE = 2;
    Term term;
    DAO dbVan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        term = intent.getParcelableExtra(ListActivity.TERM);

        dbVan = new DAO(getApplicationContext());

        final EditText wordUpdate = (EditText) findViewById(R.id.word_update);
        final EditText translationUpdate = (EditText) findViewById(R.id.translation_update);

        wordUpdate.setText(term.getWord());
        translationUpdate.setText(term.getTranslation());

        final CheckBox resetDegreeCheckBox = (CheckBox) findViewById(R.id.resetDegreeCheckBox);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myWord = wordUpdate.getText().toString().trim();
                String myTranslation = translationUpdate.getText().toString().trim();
                boolean resetDegree = resetDegreeCheckBox.isChecked();
                if(!(myWord.equals("") || myTranslation.equals(""))) {
                    int myDegree = resetDegree? 0: term.getDegree();
                    term.setWord(myWord);
                    term.setTranslation(myTranslation);
                    term.setDegree(myDegree);
                    dbVan.editTerm(term);
                    Intent result_intent = new Intent();
                    result_intent.putExtra("BAR", term);
                    setResult(RESULT_OK, result_intent);
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
        int id = item.getItemId();
        if(id == R.id.delete_word) {
            dbVan.deleteTerm(term.getID());
            Intent result_intent = new Intent();
            result_intent.putExtra("DEL", true);
            result_intent.putExtra("BAR", term);
            setResult(RESULT_OK, result_intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
