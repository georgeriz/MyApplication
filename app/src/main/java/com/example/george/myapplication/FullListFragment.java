package com.example.george.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.george.myapplication.data.Term;
import com.example.george.myapplication.data.TermAdapter;

public class FullListFragment extends Fragment {
    ListView listView;
    EditText searchEditText;
    ManageListActivity manageListActivity;
    TermAdapter termAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.words_list);
        searchEditText = (EditText) rootView.findViewById(R.id.search_list);

        manageListActivity = (ManageListActivity) getActivity();

        termAdapter = new TermAdapter(manageListActivity, R.layout.listview_item_row, manageListActivity.terms);
        listView.setAdapter(termAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Term term = ((TermAdapter)parent.getAdapter()).getItem(position);
                Intent intent = new Intent(getActivity(), EditTermActivity.class);
                intent.putExtra(ManageListActivity.TERM, term);
                getActivity().startActivityForResult(intent, EditTermActivity.EDIT_TERM_CODE);
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
