package com.example.tagsaver.utils;

import android.provider.BaseColumns;

/**
 * Created by Jeremy on 10-Jun-17.
 */

public class CategoriesContract {
    private CategoriesContract() {}

    public static class FavoriteRepos implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_FULL_NAME = "fullName";
        public static final String COLUMN_DESCRIPTION = "cObj";
        public static final String SCORE = "score";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}