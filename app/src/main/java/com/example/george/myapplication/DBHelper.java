package com.example.george.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by George on 2015-09-21.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "WordLists.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_DEGREE = "degree";
    public static final String COLUMN_LANGUAGE = "language";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WORD_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_WORD + " TEXT, " + COLUMN_TRANSLATION + " TEXT, " +
                COLUMN_DEGREE + " INTEGER, " + COLUMN_LANGUAGE + " TEXT)";
        db.execSQL(SQL_CREATE_WORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertWord (String word, String translation, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_TRANSLATION, translation);
        values.put(COLUMN_DEGREE, 0);
        values.put(COLUMN_LANGUAGE, language);

        db.insert(TABLE_NAME, null, values);
        return true;
    }

    public String[] getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT " + COLUMN_LANGUAGE + " FROM " + TABLE_NAME, null);
        Log.i(MainActivity.TAG, "cursor count: " + c.getCount());
        if(c.getCount()<1){
            return null;
        }
        int languageColumnID = c.getColumnIndex(COLUMN_LANGUAGE);
        String[] result = new String[c.getCount()];
        int i = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result[i] = c.getString(languageColumnID);
            i++;
        }
        return result;
    }

    public Term[] getList(String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_LANGUAGE + " = '" + language + "'", null);
        if(c.getCount() < 1) {
            return null;
        }
        int idColumnID = c.getColumnIndex(COLUMN_ID);
        int wordColumnID = c.getColumnIndex(COLUMN_WORD);
        int translationColumnID = c.getColumnIndex(COLUMN_TRANSLATION);
        int degreeColumnID = c.getColumnIndex(COLUMN_DEGREE);
        Term[] result = new Term[c.getCount()];
        int j = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result[j] = new Term(c.getInt(idColumnID),c.getString(wordColumnID),
                    c.getString(translationColumnID), c.getInt(degreeColumnID));
            j++;
        }
        return result;
    }

    public boolean updateDegree(int termID, int newDegree) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEGREE, newDegree);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] {String.valueOf(termID)});
        return true;
    }

    public boolean deleteWord(int termID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(termID)});
        return true;
    }

    public boolean editWord(Term term) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, term.getWord());
        values.put(COLUMN_TRANSLATION, term.getTranslation());
        values.put(COLUMN_DEGREE, term.getDegree());
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(term.getID())});
        return true;
    }

    public void editLanguage(String old_name, String new_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANGUAGE, new_name);
        db.update(TABLE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[] {old_name});
    }

    public void deleteList(String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_LANGUAGE + " = ?", new String[]{language});
    }

    public void mergeLists(String language_from, String language_to) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANGUAGE, language_to);
        db.update(TABLE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[] {language_from});
    }
}
