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

public class GeneralFragment extends Fragment {
    TextView progressPercentage;
    ProgressBar listProgressBar;
    ManageListActivity manageListActivity;
    EditText editArticles;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        //buttons, switches
        Button learnButton = (Button) rootView.findViewById(R.id.learnButton);
        Button addButton = (Button) rootView.findViewById(R.id.addButton);
        Button resetButton = (Button) rootView.findViewById(R.id.resetLearningProgressButton);

        manageListActivity = (ManageListActivity) getActivity();

        learnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LearnActivity.class);
                intent.putExtra(ManageListActivity.LIST_NAME, manageListActivity.getList_name());
                intent.putExtra(ManageListActivity.ARTICLES, manageListActivity.getArticles());
                getActivity().startActivityForResult(intent, LearnActivity.LEARN_CODE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWordActivity.class);
                intent.putExtra(ManageListActivity.LIST_NAME, manageListActivity.getList_name());
                getActivity().startActivityForResult(intent, AddWordActivity.ADD_TERMS_CODE);

            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageListActivity.resetLearningProgress();
                for (Term t : manageListActivity.terms) {
                    t.setDegree(0);
                }
                manageListActivity.updateTerms();
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
                    manageListActivity.addPrefix(article);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        editArticles.setText(manageListActivity.article);
        updateProgress(manageListActivity.getSize(), manageListActivity.getProgress());
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
