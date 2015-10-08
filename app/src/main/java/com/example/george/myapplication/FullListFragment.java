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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by George on 2015-10-08.
 */
public class FullListFragment extends Fragment {
    ListView listView;
    EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_list, container, false);

        listView = (ListView) rootView.findViewById(R.id.words_list);
        searchEditText = (EditText) rootView.findViewById(R.id.search_list);

        updateList();

        return rootView;
    }

    private void updateList() {
        DBHelper dbHelper = new DBHelper(getActivity());
        ListActivity listActivity = (ListActivity) getActivity();
        final Term[] terms = dbHelper.getList(listActivity.getList_name());

        //full list
        final String[] words = new String[terms.length];
        for(int j = 0; j < terms.length; j++) {
            words[j] = terms[j].getWord();
        }
        final ArrayAdapter<String> words_array_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, words);
        listView.setAdapter(words_array_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent open_edit_activity_intent = new Intent(getActivity(), EditActivity.class);
                Object o = parent.getItemAtPosition(position);
                String s = o.toString();
                Term term = null;
                for(int j = 0; j < words.length; j++){
                    if(s.equals(words[j])){
                        term = terms[j];
                    }
                }
                open_edit_activity_intent.putExtra(ListActivity.EXTRA_NAME_TERM, term);
                startActivity(open_edit_activity_intent);
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
                words_array_adapter.getFilter().filter(searchText);
            }
        });
    }

}
