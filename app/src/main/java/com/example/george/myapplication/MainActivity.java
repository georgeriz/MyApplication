package com.example.george.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "myapp_info";
    ArrayAdapter<String> list_names_array_adapter;
    ArrayList<String> list_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        String[] lists = dbHelper.getLists();
        if (lists == null) {
            list_names = new ArrayList<>();
        } else {
            list_names = new ArrayList<>(Arrays.asList(lists));
        }

        list_names_array_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list_names);

        ListView list_name_listView = (ListView) findViewById(R.id.languagesListView);
        list_name_listView.setAdapter(list_names_array_adapter);

        list_name_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter = (ArrayAdapter) parent.getAdapter();
                String list_name = arrayAdapter.getItem(position);
                BasicFunctions.openActivity(MainActivity.this, ListActivity.class, list_name);
            }
        });

        list_name_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> arrayAdapter = (ArrayAdapter) parent.getAdapter();
                String list_name = arrayAdapter.getItem(position);
                ActionsOnListFragment actionsOnListFragment = ActionsOnListFragment.newInstance(list_name);
                actionsOnListFragment.show(getFragmentManager(), "actions_on_list");
                return false;
            }
        });
    }

    public void updateAdapter() {
        list_names_array_adapter.notifyDataSetChanged();
    }

    public void doMergeSuccessful(String language_from) {
        list_names.remove(language_from);
        updateAdapter();
    }

    public void doAddListSuccessful(String list_name) {
        list_names.add(list_name);
        updateAdapter();
    }

    public void doDeleteSuccessful(String list_name) {
        list_names.remove(list_name);
        updateAdapter();
    }

    public void doRenameSuccessful(String language_from, String language_to) {
        list_names.set(list_names.indexOf(language_from), language_to);
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
                AddListFragment addListFragment = new AddListFragment();
                addListFragment.show(getFragmentManager(), "add_list");
                break;
            case R.id.action_merge:
                if (!list_names.isEmpty()) {
                    MergeDialogFragment mergeDialogFragment = new MergeDialogFragment();
                    mergeDialogFragment.show(getFragmentManager(), "merge");
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
