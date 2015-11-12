package com.example.george.myapplication.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.george.myapplication.R;
import com.example.george.myapplication.data.Term;

import java.util.ArrayList;

/**
 * Created by George on 2015-10-09.
 * Custom Adapter for ListView
 */
public class TermAdapter extends ArrayAdapter<Term> {
    Context context;
    int layoutResourceID;
    ArrayList<Term> terms;
    ArrayList<Term> original_terms;

    public TermAdapter(Context context, int layoutResourceID, ArrayList<Term> terms) {
        super(context, layoutResourceID, terms);
        this.context = context;
        this.layoutResourceID = layoutResourceID;
        this.terms = terms;
        original_terms = this.terms;
    }

    @Override
    public int getCount() {
        return terms.size();
    }

    @Override
    public Term getItem(int position) {
        return terms.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TermHolder holder;

        if (row==null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);

            holder = new TermHolder();
            holder.wordText = (TextView) row.findViewById(R.id.word_text);
            holder.translationText = (TextView) row.findViewById(R.id.translation_text);
            row.setTag(holder);
        } else {
            holder = (TermHolder) row.getTag();
        }

        Term term = terms.get(position);
        holder.wordText.setText(term.getWord());
        holder.translationText.setText(term.getTranslation());

        row.setBackgroundColor(Color.WHITE);
        if(term.getDegree() == 1000)
            row.setBackgroundColor(Color.GREEN);

        return row;
    }

    static class TermHolder {
        TextView wordText;
        TextView translationText;
    }

    /*public void replaceTerms(Term[] newTerms) {
        terms = new ArrayList<>(Arrays.asList(newTerms));
        original_terms = terms;
    }*/

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    //No filter, return everything
                    results.values = original_terms;
                    results.count = original_terms.size();
                } else {
                    ArrayList<Term> filtered_terms = new ArrayList<>();
                    for(Term term:original_terms) {
                        if(term.getWord().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            filtered_terms.add(term);
                        }
                    }
                    results.values = filtered_terms;
                    results.count = filtered_terms.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                terms = (ArrayList<Term>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
