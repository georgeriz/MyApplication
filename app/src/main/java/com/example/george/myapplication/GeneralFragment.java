package com.example.george.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.george.myapplication.data.Term;

import java.util.List;

public class GeneralFragment extends Fragment {
    TextView progressPercentage;
    ProgressBar listProgressBar;
    ListActivity listActivity;
    EditText editArticles;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        //buttons, switches
        Button learnButton = (Button) rootView.findViewById(R.id.learnButton);
        Button addButton = (Button) rootView.findViewById(R.id.addButton);
        Button resetButton = (Button) rootView.findViewById(R.id.resetLearningProgressButton);

        listActivity = (ListActivity) getActivity();

        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnActivity.class);
                intent.putExtra(ListActivity.LIST_NAME, listActivity.getList_name());
                intent.putExtra(ListActivity.ARTICLES, listActivity.getArticles());
                getActivity().startActivityForResult(intent, LearnActivity.LEARN_CODE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(ListActivity.LIST_NAME, listActivity.getList_name());
                getActivity().startActivityForResult(intent, AddActivity.ADD_TERMS_CODE);

            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listActivity.resetLearningProgress();
                for (Term t : listActivity.terms) {
                    t.setDegree(0);
                }
                listActivity.updateTerms();
            }
        });

        //other
        progressPercentage = (TextView) rootView.findViewById(R.id.progress_percentage);
        listProgressBar = (ProgressBar) rootView.findViewById(R.id.listProgressBar);

        //articles
        editArticles = (EditText) rootView.findViewById(R.id.edit_articles);
        Button saveArticlesButton = (Button) rootView.findViewById(R.id.save_articles_button);

        saveArticlesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String article = editArticles.getText().toString().trim();
                if (!article.equals(""))
                    listActivity.addPrefix(article);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        editArticles.setText(listActivity.article);
        updateProgress(listActivity.getSize(), listActivity.getProgress());
    }

    public void updateProgress(int max, int progress) {
        //learning progress
        listProgressBar.setMax(max);
        listProgressBar.setProgress(progress);

        //total words percentage
        String progressPercentageText = progress + "/" + max;
        progressPercentage.setText(progressPercentageText);
    }
}
