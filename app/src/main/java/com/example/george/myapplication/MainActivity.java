package com.example.george.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "myapp_info";
    final static String LIST_NAME = "com.example.george.myapplication.LIST_NAME";
    final static String STATE_LIST_NAMES = "state_list_names";
    final static int UPDATE_LIST_NAMES = 1;
    static DBHelper dbHelper;
    ArrayAdapter<String> list_names_array_adapter;
    static ArrayList<String> list_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //databases
        if(savedInstanceState==null) {
            dbHelper = new DBHelper(getApplicationContext());
            list_names = new ArrayList<>(Arrays.asList(dbHelper.getLists()));
        } else {
            list_names = new ArrayList<>(savedInstanceState.getStringArrayList(STATE_LIST_NAMES));
        }
        if(list_names==null) { return;}

        list_names_array_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list_names);

        ListView list_name_listView = (ListView) findViewById(R.id.languagesListView);
        list_name_listView.setAdapter(list_names_array_adapter);

        //listView click listener
        list_name_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent open_list_activity_intent = new Intent(getApplicationContext(), ListActivity.class);
                //get which list was selected
                ArrayAdapter<String> arrayAdapter = (ArrayAdapter) parent.getAdapter();
                String list_name = arrayAdapter.getItem(position);
                //alternative
                //String list_name = list_names_array_adapter.getItem(position);
                open_list_activity_intent.putExtra(LIST_NAME, list_name);
                startActivityForResult(open_list_activity_intent, UPDATE_LIST_NAMES);
            }
        });

    }

    public void updateListNames() {
        list_names.clear();
        list_names.addAll(Arrays.asList(dbHelper.getLists()));
        updateAdapter();
    }
    public void updateAdapter() {
        list_names_array_adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_add:
                Intent open_add_list_intent = new Intent(getApplicationContext(), AddActivity.class);
                open_add_list_intent.putExtra(LIST_NAME, "");
                startActivityForResult(open_add_list_intent, UPDATE_LIST_NAMES);
                break;
            case R.id.action_merge:
                MergeDialogFragment mergeDialogFragment = new MergeDialogFragment();
                mergeDialogFragment.show(getFragmentManager(), "merge");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putStringArrayList(STATE_LIST_NAMES, list_names);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_LIST_NAMES && resultCode == RESULT_OK) {
            updateListNames();
        }
    }

    public static class MergeDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_main_merge, null);
            builder.setView(dialogView);
            final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner1);
            final Spinner spinner2 = (Spinner) dialogView.findViewById(R.id.spinner2);
            final ArrayAdapter<String> list_names_array_adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, list_names);
            spinner1.setAdapter(list_names_array_adapter);
            spinner2.setAdapter(list_names_array_adapter);
            builder.setMessage("Select two lists");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String language_from = spinner1.getSelectedItem().toString();
                    String language_to = spinner2.getSelectedItem().toString();
                    if(language_from.equals(language_to))
                        return;
                    Term[] terms_from = dbHelper.getList(language_from);
                    Term[] terms_to = dbHelper.getList(language_to);
                    for(Term term_from: terms_from){
                        for(Term term_to: terms_to){
                            if(term_from.getWord().equals(term_to.getWord())){
                                //update translation of term_to and delete term_from
                                String new_translation = term_to.getTranslation() + ", " + term_from.getTranslation();
                                term_to.setTranslation(new_translation);
                                int new_degree = Math.min(term_from.getDegree(), term_to.getDegree());
                                term_to.setDegree(new_degree);
                                dbHelper.editWord(term_to);
                                dbHelper.deleteWord(term_from.getID());
                                break;
                            }
                        }
                    }
                    dbHelper.mergeLists(language_from, language_to);
                    list_names.remove(language_from);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.updateAdapter();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });
            return builder.create();
        }
    }
}
