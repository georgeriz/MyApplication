package com.example.george.myapplication;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by George on 2015-10-10.
 * Functions used by the application.
 */
public final class BasicFunctions {

    final static String LIST_NAME = "com.example.george.myapplication.LIST_NAME";

    static final String EXTRA_NAME_TERM = "com.example.george.myapplicaiton.TERM_EXTRA";

    final static int EDIT_TERM = 1;

    final static int ADD_TERMS = 2;

    private BasicFunctions() {
    }

    public static void openActivity(Activity activity, Class<?> cls) {
        Intent open_activity_intent = new Intent(activity, cls);
        activity.startActivity(open_activity_intent);
    }

    public static void openActivity(Activity activity, Class<?> cls, String list_name) {
        Intent open_activity_intent = new Intent(activity, cls);
        open_activity_intent.putExtra(LIST_NAME, list_name);
        activity.startActivity(open_activity_intent);
    }

    public static void openActivityForResult(Activity activity, Class<?> cls, String list_name) {
        Intent open_activity_intent = new Intent(activity, cls);
        open_activity_intent.putExtra(LIST_NAME, list_name);
        activity.startActivityForResult(open_activity_intent, ADD_TERMS);
    }

    public static void openActivityForResultWithTerm(Activity activity, Class<?> cls, Term term) {
        Intent open_list_activity_intent = new Intent(activity, cls);
        open_list_activity_intent.putExtra(EXTRA_NAME_TERM, term);
        activity.startActivityForResult(open_list_activity_intent, EDIT_TERM);
    }

    public static void mergeFromToLanguageSafe(Activity activity, String language_from, String language_to) {
        DBHelper dbHelper = new DBHelper(activity);
        Term[] terms_from = dbHelper.getList(language_from);
        Term[] terms_to = dbHelper.getList(language_to);
        if (terms_from != null && terms_to != null)
            for (Term term_from : terms_from) {
                for (Term term_to : terms_to) {
                    if (term_from.getWord().equals(term_to.getWord())) {
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

    public static boolean renameFromToLanguage(Activity activity, String language_from, String language_to) {
        DBHelper dbHelper = new DBHelper(activity);
        dbHelper.renameList(language_from, language_to);
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

    public static void addList(Activity activity, String list_name) {
        DBHelper dbHelper = new DBHelper(activity);
        dbHelper.addList(list_name);
    }
}
