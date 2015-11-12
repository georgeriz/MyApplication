package com.example.george.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.george.myapplication.data.BasicFunctions;
import com.example.george.myapplication.data.DBHelper;
import com.example.george.myapplication.data.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    static final String LIST_NAME = "list_name";
    static final String STATE_TERMS_LIST = "state_terms_list";
    String list_name;
    DBHelper dbHelper;
    ViewPager viewPager;
    ArrayList<Term> terms;
    FragmentAdapter mAdapter;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager) findViewById(R.id.activity_list_view_pager);

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
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            list_name = intent.getStringExtra(BasicFunctions.LIST_NAME);

            dbHelper = new DBHelper(getApplicationContext());
            Term[] foo = dbHelper.getList(list_name);
            if (foo == null) {
                terms = new ArrayList<>();
            } else {
                terms = new ArrayList<>(Arrays.asList(foo));
            }
        } else {
            list_name = savedInstanceState.getString(LIST_NAME);
            terms = savedInstanceState.getParcelableArrayList(STATE_TERMS_LIST);
        }
        setTitle(list_name);
    }

    public String getList_name() {
        return list_name;
    }

    public int getProgress() {
        int progress_count = 0;
        for (Term term : terms) {
            if (term.getDegree() == 1000) {
                progress_count++;
            }
        }
        return progress_count;
    }

    public int getSize() {
        return terms.size();
    }

    public void updateTerms() {
        /*terms.clear();
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        terms.addAll(Arrays.asList(dbHelper.getList(list_name)));*/
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof GeneralFragment) {
                ((GeneralFragment) fragment).updateProgress(getSize(), getProgress());
            } else if (fragment instanceof FullListFragment) {
                ((FullListFragment) fragment).updateList();
            }
        }
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
                BasicFunctions.openActivity(ListActivity.this, SettingsActivity.class);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(LIST_NAME, list_name);
        state.putParcelableArrayList(STATE_TERMS_LIST, terms);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BasicFunctions.EDIT_TERM && resultCode == RESULT_OK) {
            boolean isDelete = data.getBooleanExtra("DEL", false);
            Term foo = data.getParcelableExtra("BAR");
            for (Term t : terms) {
                if (t.getID() == foo.getID()) {
                    if(isDelete){
                        terms.remove(t);
                    }else {
                        terms.set(terms.indexOf(t), foo);
                    }
                    break;
                }
            }
            updateTerms();
        } else if (requestCode == BasicFunctions.ADD_TERMS && resultCode == RESULT_OK) {

            ArrayList<Term> newTerms = data.getParcelableArrayListExtra("FOO");
            terms.addAll(newTerms);
            updateTerms();
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            terms.clear();
            Term[] foo = dbHelper.getList(list_name);
            if (foo != null) {
                terms.addAll(Arrays.asList(foo));
            }
            updateTerms();
        }
    }

    public class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new GeneralFragment();
            } else if (position == 1) {
                return new FullListFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
