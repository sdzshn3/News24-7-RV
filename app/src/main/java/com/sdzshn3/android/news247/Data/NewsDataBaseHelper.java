package com.sdzshn3.android.news247.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sdzshn3.android.news247.Data.FavoriteNewsContract.FavoriteNewsEntry;

public class NewsDataBaseHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = NewsDataBaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "news247.db";
    private static final int DATABASE_VERSION = 1;

    public NewsDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_NEWS247_TABLE = "CREATE TABLE " + FavoriteNewsEntry.TABLE_NAME + " ("
                + FavoriteNewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteNewsEntry.COLUMN_SECTION_NAME + " TEXT, "
                + FavoriteNewsEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoriteNewsEntry.COLUMN_ARTICLE_URL + " TEXT NOT NULL, "
                + FavoriteNewsEntry.COLUMN_API_URI + " TEXT NOT NULL, "
                + FavoriteNewsEntry.COLUMN_PUBLISHED_AT + " TEXT, "
                + FavoriteNewsEntry.COLUMN_FIRST_NAME + " TEXT, "
                + FavoriteNewsEntry.COLUMN_LAST_NAME + " TEXT, "
                + FavoriteNewsEntry.COLUMN_THUMBNAIL + " TEXT);";
        Log.v(LOG_TAG, SQL_CREATE_NEWS247_TABLE);
        db.execSQL(SQL_CREATE_NEWS247_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
