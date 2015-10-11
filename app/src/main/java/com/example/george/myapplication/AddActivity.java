package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {
    final static String STATE_LISTS_NAMES = "lists_names";
    EditText wordInput;
    EditText translationInput;
    AutoCompleteTextView languageInput;
    Button saveButton;
    String[] lists_names;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        wordInput = (EditText) findViewById(R.id.word_input);
        translationInput = (EditText) findViewById(R.id.translation_input);
        languageInput = (AutoCompleteTextView) findViewById(R.id.language_input);
        saveButton = (Button)  findViewById(R.id.save_button);

        //for the autoComplete of the languages
        if (savedInstanceState == null) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            lists_names = dbHelper.getLists();
        } else {
            lists_names = savedInstanceState.getStringArray(STATE_LISTS_NAMES);
        }
        if(lists_names==null)return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, lists_names);
        languageInput.setAdapter(adapter);
        languageInput.setThreshold(1);

        Intent intent = getIntent();
        String list_name = intent.getStringExtra(BasicFunctions.LIST_NAME);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        saveNewWord(list_name);
    }

    private void saveNewWord(final String list_name) {
        wordInput.setText("");
        wordInput.requestFocus();
        imm.showSoftInput(wordInput, InputMethodManager.SHOW_IMPLICIT);
        translationInput.setText("");
        languageInput.setText(list_name);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordInput.getText().toString().trim();
                String translation = translationInput.getText().toString().trim();
                String language = languageInput.getText().toString().trim();
                if (!(word.equals("") || translation.equals("") || language.equals(""))) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    Term[] terms = dbHelper.getList(language);
                    if (terms != null) {
                        for (Term term : terms) {
                            if (term.getWord().equals(word)) {
                                AlreadyExistsDialogFragment alreadyExistsDialogFragment = new AlreadyExistsDialogFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("myTerm", term);
                                alreadyExistsDialogFragment.setArguments(bundle);
                                alreadyExistsDialogFragment.show(getFragmentManager(), "alreadyExists");
                                saveNewWord(language);
                                return;
                            }
                        }
                    }
                    dbHelper.insertWord(word, translation, language);
                    setResult(RESULT_OK);
                    saveNewWord(language);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putStringArray(STATE_LISTS_NAMES, lists_names);
    }

    public static class AlreadyExistsDialogFragment extends DialogFragment {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BasicFunctions.UPDATE_LIST_NAMES && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
        }
    }
}
