package com.example.george.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by George on 2015-09-21.
 * Class managing the Database.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "WordLists.db";
    public static final int DATABASE_VERSION = 4;
    public static final String TABLE_NAME = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_DEGREE = "degree";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_ARTICLE = "article";

    public static final String TABLE_LANGUAGE_NAME = "languages";

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

        String SQL_CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_LANGUAGE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_LANGUAGE + " TEXT, " +
                COLUMN_ARTICLE + " TEXT)";
        db.execSQL(SQL_CREATE_LANGUAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE_NAME);
        onCreate(db);
    }

    public int insertWord (String word, String translation, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_TRANSLATION, translation);
        values.put(COLUMN_DEGREE, 0);
        values.put(COLUMN_LANGUAGE, language);

        return (int) db.insert(TABLE_NAME, null, values);
    }

    public String[] getLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COLUMN_LANGUAGE + " FROM " + TABLE_LANGUAGE_NAME, null);
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
        c.close();
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
        c.close();
        return result;
    }

    public Term[] getListWithUnlearned(String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_LANGUAGE + " = '" + language + "' AND " +
                COLUMN_DEGREE + " < 1000", null);
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
        c.close();
        return result;
    }

    public boolean updateDegree(int termID, int newDegree) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEGREE, newDegree);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(termID)});
        return true;
    }

    public boolean resetLearningProcess(String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEGREE, 0);
        db.update(TABLE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[] {language});
        return true;
    }

    public boolean deleteWord(int termID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(termID)});
        db.close();
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


    public void deleteList(String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_LANGUAGE + " = ?", new String[]{language});
        db.delete(TABLE_LANGUAGE_NAME, COLUMN_LANGUAGE + " = ?", new String[]{language});
    }


    public void mergeLists(String language_from, String language_to) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANGUAGE, language_to);
        db.update(TABLE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[]{language_from});
        db.delete(TABLE_LANGUAGE_NAME, COLUMN_LANGUAGE + " = ?", new String[]{language_from});
    }

    public void renameList(String language_from, String language_to) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANGUAGE, language_to);
        db.update(TABLE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[]{language_from});
        db.update(TABLE_LANGUAGE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[]{language_from});
    }

    public void addList(String list_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LANGUAGE, list_name);
        db.insert(TABLE_LANGUAGE_NAME, null, values);
    }

    public void addPrefix(String prefix, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTICLE, prefix);
        db.update(TABLE_LANGUAGE_NAME, values, COLUMN_LANGUAGE + " = ?", new String[]{language});
    }

    public String getPrefix(String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COLUMN_ARTICLE + " FROM " + TABLE_LANGUAGE_NAME +
                " WHERE " + COLUMN_LANGUAGE + " = '" + language + "'", null);
        if(c.getCount() < 1) {
            return null;
        }
        int articleColumnID = c.getColumnIndex(COLUMN_ARTICLE);
        c.moveToFirst();
        String result = c.getString(articleColumnID);
        c.close();
        return result;
    }
}
