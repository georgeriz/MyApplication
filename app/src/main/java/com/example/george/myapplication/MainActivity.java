package com.example.george.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "myapp_info";
    final static String STATE_LIST_NAMES = "state_list_names";
    final static String UPDATE_PREVIOUS = "update_previous";
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
        if(list_names.isEmpty()) { return;}

        list_names_array_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list_names);

        ListView list_name_listView = (ListView) findViewById(R.id.languagesListView);
        list_name_listView.setAdapter(list_names_array_adapter);

        //listView click listener
        list_name_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter = (ArrayAdapter) parent.getAdapter();
                String list_name = arrayAdapter.getItem(position);
                BasicFunctions.openActivityForResult(MainActivity.this, ListActivity.class, list_name);
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
                BasicFunctions.openActivity(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.action_add:
                BasicFunctions.openActivityForResult(MainActivity.this, AddActivity.class, "");
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
        if (requestCode == BasicFunctions.UPDATE_LIST_NAMES && resultCode == RESULT_OK) {
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
            builder.setTitle("Select two lists");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String language_from = spinner1.getSelectedItem().toString();
                    String language_to = spinner2.getSelectedItem().toString();
                    if (language_from.equals(language_to)) {
                        Toast.makeText(getActivity(), "Select two different lists", Toast.LENGTH_LONG)
                        .show();
                    }else{
                        BasicFunctions.mergeFromToLanguageSafe(getActivity(), language_from, language_to);
                        //actions in order to update the ListView
                        list_names.remove(language_from);
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.updateAdapter();
                    }
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
