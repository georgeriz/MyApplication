package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.george.myapplication.data.DAO;
import com.example.george.myapplication.data.Term;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
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
        list_name = intent.getStringExtra(ListActivity.LIST_NAME);

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
                                /*AlreadyExistsDialogFragment alreadyExistsDialogFragment = new AlreadyExistsDialogFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("myTerm", term);
                                alreadyExistsDialogFragment.setArguments(bundle);
                                alreadyExistsDialogFragment.show(getFragmentManager(), "alreadyExists");*/
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
                    Log.i(MainActivity.TAG, "add activity");
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

    /*public static class AlreadyExistsDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final Term term = getArguments().getParcelable("myTerm");
            builder.setTitle("Word already exists");
            builder.setMessage("This word already exists, so it cannot be added.\n" +
                    "Do you wish to edit existing word?");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BasicFunctions.openActivityForResultWithTerm(getActivity(), EditActivity.class, term);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing
                }
            });
            return builder.create();
        }
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BasicFunctions.EDIT_TERM && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
        }
    }*/
}
