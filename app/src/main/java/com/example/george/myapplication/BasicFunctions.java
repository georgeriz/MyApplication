package com.example.george.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by George on 2015-10-10.
 */
public final class BasicFunctions {

    final static String LIST_NAME = "com.example.george.myapplication.LIST_NAME";

    static final String EXTRA_NAME_TERM = "com.example.george.myapplicaiton.TERM_EXTRA";

    final static int UPDATE_LIST_NAMES = 1;

    private BasicFunctions(){}

    public static void openActivityForResult(Activity activity, Class<?> cls, String list_name) {
        Intent open_activity_intent = new Intent(activity, cls);
        open_activity_intent.putExtra(LIST_NAME, list_name);
        activity.startActivityForResult(open_activity_intent, UPDATE_LIST_NAMES);
    }

    public static void openActivityForResultWithTerm(Activity activity, Class<?> cls, Term term) {
        Intent open_list_activity_intent = new Intent(activity, cls);
        open_list_activity_intent.putExtra(EXTRA_NAME_TERM, term);
        activity.startActivityForResult(open_list_activity_intent, UPDATE_LIST_NAMES);
    }

    public static void mergeFromToLanguageSafe(Activity activity, String language_from, String language_to) {
        if (language_from.equals(language_to)) {
            return;
        }
        DBHelper dbHelper = new DBHelper(activity);
        Term[] terms_from = dbHelper.getList(language_from);
        Term[] terms_to = dbHelper.getList(language_to);
        for(Term term_from: terms_from){
            for(Term term_to: terms_to){
                if(term_from.getWord().equals(term_to.getWord())){
                    //update translation of term_to and delete term_from
                    String new_translation = term_to.getTranslation() + ", " + term_from.getTranslation();
                    term_to.setTranslation(new_translation);
                    int new_degree = Math.min(term_from.getDegree(), term_to.getDegree());
                    term_to.setDegree(new_degree);
                    dbHelper.editWord(term_to);
                    dbHelper.deleteWord(term_from.getID());
                    break;
                }
            }
        }
        dbHelper.mergeLists(language_from, language_to);
    }

    public static boolean renameFromToLanguageCheck(Activity activity, String language_from, String language_to) {
        DBHelper dbHelper = new DBHelper(activity);
        String[] lists_name = dbHelper.getLists();
        for(String a_list_name:lists_name){
            if(a_list_name.equals(language_to)){
                Toast.makeText(activity, "This name already exists." +
                        "Try merging the two lists instead.", Toast.LENGTH_LONG)
                        .show();
                return false;
            }
        }
        dbHelper.mergeLists(language_from, language_to);
        return true;
    }

    public static void deleteList(Activity activity, String list_name) {
        DBHelper dbHelper = new DBHelper(activity);
        dbHelper.deleteList(list_name);
    }

    public static void deleteTerm(Activity activity, Term term) {
        DBHelper dbHelper = new DBHelper(activity);
        dbHelper.deleteWord(term.getID());
    }
}
