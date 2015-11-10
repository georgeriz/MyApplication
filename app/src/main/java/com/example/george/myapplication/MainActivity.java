package com.example.george.myapplication;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "myapp_info";
    final static String STATE_LIST_NAMES = "state_list_names";
    final static String UPDATE_PREVIOUS = "update_previous";
    static DBHelper dbHelper;
    ArrayAdapter<String> list_names_array_adapter;
    ArrayList<String> list_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        String[] lists = dbHelper.getLists();
        if (lists == null) {
            //deal with that
            return;
        }

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

    /*public void updateListNames() {
        list_names.clear();
        list_names.addAll(Arrays.asList(dbHelper.getLists()));
        updateAdapter();
    }*/

    public void updateAdapter() {
        list_names_array_adapter.notifyDataSetChanged();
    }

    public void doMergeSuccessful(String language_from) {
        list_names.remove(language_from);
        updateAdapter();
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
                AddListFragment addListFragment = AddListFragment.newInstance((String[])list_names.toArray());
                addListFragment.show(getFragmentManager(), "add_list");
                break;
            case R.id.action_merge:
                MergeDialogFragment mergeDialogFragment = MergeDialogFragment.newInstance((String[])list_names.toArray());
                mergeDialogFragment.show(getFragmentManager(), "merge");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
