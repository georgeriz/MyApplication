package com.example.george.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "myapp_info";
    final static String LIST_NAME = "com.example.george.myapplication.LIST_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //databases
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        String[] list_names = dbHelper.getLists();

        if(list_names!=null) {

            final ArrayAdapter<String> list_names_array_adapter = new ArrayAdapter<String>(this,
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
                    startActivity(open_list_activity_intent);
                }
            });
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.action_add) {
            Intent open_add_list_intent = new Intent(getApplicationContext(), AddActivity.class);
            open_add_list_intent.putExtra(LIST_NAME, "");
            startActivity(open_add_list_intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
