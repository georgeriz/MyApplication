package com.example.george.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

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
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.activity_list_view_pager);

        FragmentAdapter mAdapter = new FragmentAdapter(getSupportFragmentManager());

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        actionBar.addTab(actionBar.newTab().setText("General").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Full list").setTabListener(tabListener));



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(mAdapter);

        //selected list
        Intent intent = getIntent();
        list_name = intent.getStringExtra(MainActivity.LIST_NAME);
        setTitle(list_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new GeneralFragment();
            } else if (position == 1){
                return new FullListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public String getList_name() {
        return list_name;
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
