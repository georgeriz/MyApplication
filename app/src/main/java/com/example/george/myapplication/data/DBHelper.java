package com.example.george.myapplication.data;

import android.content.Context;
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
}
