package com.example.george.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by George on 2015-11-12.
 * Data Access Object
 */
public class DAO {
    private SQLiteDatabase db;

    public DAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    public String[] getLists() {
        Cursor c = db.rawQuery("SELECT " + DBHelper.COLUMN_LANGUAGE + " FROM "
                + DBHelper.TABLE_LANGUAGE_NAME, null);
        if(c.getCount()<1){
            return null;
        }
        int languageColumnID = c.getColumnIndex(DBHelper.COLUMN_LANGUAGE);
        String[] result = new String[c.getCount()];
        int i = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result[i] = c.getString(languageColumnID);
            i++;
        }
        c.close();
        return result;
    }

    public Term[] getList(String language) {
        Cursor c = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME +
                " WHERE " + DBHelper.COLUMN_LANGUAGE + " = '" + language + "'", null);
        if(c.getCount() < 1) {
            return null;
        }
        int idColumnID = c.getColumnIndex(DBHelper.COLUMN_ID);
        int wordColumnID = c.getColumnIndex(DBHelper.COLUMN_WORD);
        int translationColumnID = c.getColumnIndex(DBHelper.COLUMN_TRANSLATION);
        int degreeColumnID = c.getColumnIndex(DBHelper.COLUMN_DEGREE);
        Term[] result = new Term[c.getCount()];
        int j = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result[j] = new Term(c.getInt(idColumnID),c.getString(wordColumnID),
                    c.getString(translationColumnID), c.getInt(degreeColumnID));
            j++;
        }
        c.close();
        return result;
    }

    public Term[] getListWithUnlearned(String language) {
        Cursor c = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME +
                " WHERE " + DBHelper.COLUMN_LANGUAGE + " = '" + language + "' AND " +
                DBHelper.COLUMN_DEGREE + " < 1000", null);
        if(c.getCount() < 1) {
            return null;
        }
        int idColumnID = c.getColumnIndex(DBHelper.COLUMN_ID);
        int wordColumnID = c.getColumnIndex(DBHelper.COLUMN_WORD);
        int translationColumnID = c.getColumnIndex(DBHelper.COLUMN_TRANSLATION);
        int degreeColumnID = c.getColumnIndex(DBHelper.COLUMN_DEGREE);
        Term[] result = new Term[c.getCount()];
        int j = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result[j] = new Term(c.getInt(idColumnID),c.getString(wordColumnID),
                    c.getString(translationColumnID), c.getInt(degreeColumnID));
            j++;
        }
        c.close();
        return result;
    }

    public String getPrefix(String language) {
        Cursor c = db.rawQuery("SELECT " + DBHelper.COLUMN_ARTICLE + " FROM " +
                DBHelper.TABLE_LANGUAGE_NAME + " WHERE " +
                DBHelper.COLUMN_LANGUAGE + " = '" + language + "'", null);
        if(c.getCount() < 1) {
            return null;
        }
        int articleColumnID = c.getColumnIndex(DBHelper.COLUMN_ARTICLE);
        c.moveToFirst();
        String result = c.getString(articleColumnID);
        c.close();
        return result;
    }

    public int addWord (String word, String translation, String language) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_WORD, word);
        values.put(DBHelper.COLUMN_TRANSLATION, translation);
        values.put(DBHelper.COLUMN_DEGREE, 0);
        values.put(DBHelper.COLUMN_LANGUAGE, language);
        return (int) db.insert(DBHelper.TABLE_NAME, null, values);
    }

    public int updateDegree(int termID, int newDegree) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DEGREE, newDegree);
        return db.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(termID)});
    }

    public int resetLearningProcess(String language) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DEGREE, 0);
        return db.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language});
    }

    public int deleteTerm(int termID) {
        return db.delete(DBHelper.TABLE_NAME, DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(termID)});
    }

    public int editTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_WORD, term.getWord());
        values.put(DBHelper.COLUMN_TRANSLATION, term.getTranslation());
        values.put(DBHelper.COLUMN_DEGREE, term.getDegree());
        return db.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(term.getID())});
    }

    public void deleteList(String language) {
        db.delete(DBHelper.TABLE_NAME, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language});
        db.delete(DBHelper.TABLE_LANGUAGE_NAME, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language});
    }

    public void mergeLists(String language_from, String language_to) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LANGUAGE, language_to);
        db.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language_from});
        db.delete(DBHelper.TABLE_LANGUAGE_NAME, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language_from});
    }

    public void mergeListsWithCommonWords(String language_from, String language_to) {
        Term[] terms_from = getList(language_from);
        Term[] terms_to = getList(language_to);
        if(terms_from != null && terms_to != null)
            for (Term term_from : terms_from)
                for (Term term_to : terms_to)
                    if (term_from.getWord().equals(term_to.getWord())) {
                        //update translation of term_to and delete term_from
                        String new_translation = term_to.getTranslation() + ", " + term_from.getTranslation();
                        term_to.setTranslation(new_translation);
                        int new_degree = Math.min(term_from.getDegree(), term_to.getDegree());
                        term_to.setDegree(new_degree);
                        editTerm(term_to);
                        deleteTerm(term_from.getID());
                        break;
                    }
        mergeLists(language_from, language_to);
    }

    public void renameList(String language_from, String language_to) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LANGUAGE, language_to);
        db.update(DBHelper.TABLE_NAME, values, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language_from});
        db.update(DBHelper.TABLE_LANGUAGE_NAME, values, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language_from});
    }

    public int addList(String list_name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_LANGUAGE, list_name);
        return (int) db.insert(DBHelper.TABLE_LANGUAGE_NAME, null, values);
    }

    public int addPrefix(String prefix, String language) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ARTICLE, prefix);
        return db.update(DBHelper.TABLE_LANGUAGE_NAME, values, DBHelper.COLUMN_LANGUAGE + " = ?",
                new String[]{language});
    }
}
