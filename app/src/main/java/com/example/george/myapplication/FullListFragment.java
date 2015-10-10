package com.example.george.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by George on 2015-10-08.
 */
public class FullListFragment extends Fragment {
    ListView listView;
    EditText searchEditText;
    ListActivity listActivity;
    TermAdapter termAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.words_list);
        searchEditText = (EditText) rootView.findViewById(R.id.search_list);

        listActivity = (ListActivity) getActivity();

        //Term[] terms = listActivity.getTerms();
        termAdapter = new TermAdapter(listActivity, R.layout.listview_item_row, listActivity.terms);
        listView.setAdapter(termAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Term term = ((TermAdapter)parent.getAdapter()).getItem(position);
                BasicFunctions.openActivityForResultWithTerm(getActivity(), EditActivity.class, term);
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
                termAdapter.getFilter().filter(searchText);
            }
        });

        return rootView;
    }

    public void updateList() {
        searchEditText.setText("");
        termAdapter.notifyDataSetChanged();
    }

}
