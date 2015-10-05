package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListActivity extends AppCompatActivity {
    static final String EXTRA_NAME_TERM = "com.example.george.myapplicaiton.TERM_EXTRA";
    static String list_name;
    DBHelper dbHelper;
    TextView totalWords;
    ProgressBar listProgressBar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //selected list
        Intent intent = getIntent();
        list_name = intent.getStringExtra(MainActivity.LIST_NAME);
        setTitle(list_name);

        //buttons
        Button learnButton = (Button) findViewById(R.id.learnButton);
        Button addButton = (Button) findViewById(R.id.addButton);

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

        //other
        dbHelper = new DBHelper(getApplicationContext());
        totalWords = (TextView) findViewById(R.id.total_words_text);
        listProgressBar = (ProgressBar) findViewById(R.id.listProgressBar);
        listView = (ListView) findViewById(R.id.words_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //info about the list
        final Term[] terms = dbHelper.getList(list_name);

        //error control
        if(terms==null){return;}

        //total words
        totalWords.setText(Integer.toString(terms.length));

        //learning progress
        listProgressBar.setMax(terms.length);
        int progress_count = 0;
        for(Term term: terms) {
            if(term.getDegree() == 1000) {
                progress_count++;
            }
        }
        listProgressBar.setProgress(progress_count);

        //full list
        String[] words = new String[terms.length];
        for(int j = 0; j < terms.length; j++) {
            words[j] = terms[j].getWord();
        }
        ArrayAdapter<String> words_array_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, words);
        listView.setAdapter(words_array_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent open_edit_activity_intent = new Intent(getApplicationContext(), EditActivity.class);
                open_edit_activity_intent.putExtra(EXTRA_NAME_TERM, terms[position]);
                startActivity(open_edit_activity_intent);
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
