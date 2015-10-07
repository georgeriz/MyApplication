package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Objects;

public class ListActivity extends AppCompatActivity {
    static final String EXTRA_NAME_TERM = "com.example.george.myapplicaiton.TERM_EXTRA";
    static final String SHARED_PREFERENCES = "com.example.george.myapplication.SHARED_PREFERENCES";
    static final String SHOW_TRANSLATION_SETTINGS = "show_translation";
    static String list_name;
    DBHelper dbHelper;
    TextView progressPercentage;
    ProgressBar listProgressBar;
    ListView listView;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //selected list
        Intent intent = getIntent();
        list_name = intent.getStringExtra(MainActivity.LIST_NAME);
        setTitle(list_name);

        //get preferences
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
        boolean showTranslation = settings.getBoolean(SHOW_TRANSLATION_SETTINGS, false);

        //buttons, switches
        Button learnButton = (Button) findViewById(R.id.learnButton);
        Button addButton = (Button) findViewById(R.id.addButton);
        Switch showTranslationSwitch = (Switch) findViewById(R.id.show_translation_switch);

        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity_intent(LearnActivity.class, list_name);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_activity_intent(AddActivity.class, list_name);
            }
        });

        showTranslationSwitch.setChecked(showTranslation);
        showTranslationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putBoolean(SHOW_TRANSLATION_SETTINGS, isChecked);
                settingsEditor.commit();
            }
        });

        //other
        dbHelper = new DBHelper(getApplicationContext());
        progressPercentage = (TextView) findViewById(R.id.progress_percentage);
        listProgressBar = (ProgressBar) findViewById(R.id.listProgressBar);
        listView = (ListView) findViewById(R.id.words_list);
        searchEditText = (EditText) findViewById(R.id.search_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //info about the list
        final Term[] terms = dbHelper.getList(list_name);

        //error control
        if(terms==null){return;}

        //learning progress
        listProgressBar.setMax(terms.length);
        int progress_count = 0;
        for(Term term: terms) {
            if(term.getDegree() == 1000) {
                progress_count++;
            }
        }
        listProgressBar.setProgress(progress_count);

        //total words percentage
        String progressPercentageText = progress_count + "/" + terms.length;
        progressPercentage.setText(progressPercentageText);

        //full list
        final String[] words = new String[terms.length];
        for(int j = 0; j < terms.length; j++) {
            words[j] = terms[j].getWord();
        }
        final ArrayAdapter<String> words_array_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, words);
        listView.setAdapter(words_array_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent open_edit_activity_intent = new Intent(getApplicationContext(), EditActivity.class);
                Object o = parent.getItemAtPosition(position);
                String s = o.toString();
                Term term = null;
                for(int j = 0; j < words.length; j++){
                    if(s.equals(words[j])){
                        term = terms[j];
                    }
                }
                open_edit_activity_intent.putExtra(EXTRA_NAME_TERM, term);
                startActivity(open_edit_activity_intent);
            }
        });

        //search list
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchEditText.getText().toString();
                words_array_adapter.getFilter().filter(searchText);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_rename:
                RenameDialogFragment renameDialogFragment = new RenameDialogFragment();
                renameDialogFragment.show(getFragmentManager(), "rename");
                break;
            case R.id.action_delete:
                dbHelper.deleteList(list_name);
                finish();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void open_activity_intent(Class<?> cls, String list_name) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.putExtra(MainActivity.LIST_NAME, list_name);
        startActivity(intent);
    }

    public static class RenameDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final EditText listNameEditText = new EditText(getActivity());
            listNameEditText.setText(list_name);
            builder.setView(listNameEditText);
            builder.setMessage("Edit name")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String new_list_name = listNameEditText.getText().toString().trim();
                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.editLanguage(list_name, new_list_name);
                            list_name = new_list_name;
                            getActivity().setTitle(list_name);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();

        }
    }
}
