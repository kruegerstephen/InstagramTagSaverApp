package com.example.tagsaver.utils;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Jeremy on 10-Jun-17.
 */

public class TagsDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "githubSearch.db";
    private static final int DATABASE_VERSION = 1;

    public TagsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_REPOS_TABLE =
                "CREATE TABLE " + CategoriesContract.FavoriteRepos.TABLE_NAME + " (" +
                        CategoriesContract.FavoriteRepos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                        CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION + " TEXT, " +
                        CategoriesContract.FavoriteRepos.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                        ");";
        db.execSQL(SQL_CREATE_FAVORITE_REPOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesContract.FavoriteRepos.TABLE_NAME);
        onCreate(db);
    }
}