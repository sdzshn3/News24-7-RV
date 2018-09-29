package com.sdzshn3.android.news247.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sdzshn3.android.news247.Data.FavoriteNewsContract.FavoriteNewsEntry;

public class NewsProvider extends ContentProvider {

    private static final int NEWS = 100;
    private static final int NEWS_ID = 101;
    private static final String LOG_TAG = NewsProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FavoriteNewsContract.CONTENT_AUTHORITY, FavoriteNewsContract.PATH_NEWS, NEWS);
        sUriMatcher.addURI(FavoriteNewsContract.CONTENT_AUTHORITY, FavoriteNewsContract.PATH_NEWS + "/#", NEWS_ID);
    }

    private NewsDataBaseHelper newsDataBaseHelper;

    @Override
    public boolean onCreate() {
        newsDataBaseHelper = new NewsDataBaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = newsDataBaseHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                cursor = database.query(FavoriteNewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NEWS_ID:
                selection = FavoriteNewsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FavoriteNewsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return FavoriteNewsEntry.CONTENT_LIST_TYPE;
            case NEWS_ID:
                return FavoriteNewsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI \" + uri + \" with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return insertArticle(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertArticle(Uri uri, ContentValues values) {
        String title = values.getAsString(FavoriteNewsEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Title is required");
        }
        String articleUrl = values.getAsString(FavoriteNewsEntry.COLUMN_ARTICLE_URL);
        if (articleUrl == null) {
            throw new IllegalArgumentException("article URL is required");
        }
        String apiUri = values.getAsString(FavoriteNewsEntry.COLUMN_API_URI);
        if (apiUri == null) {
            throw new IllegalArgumentException("API URI is required");
        }

        SQLiteDatabase database = newsDataBaseHelper.getWritableDatabase();
        long id = database.insert(FavoriteNewsEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = newsDataBaseHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                rowsDeleted = database.delete(FavoriteNewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NEWS_ID:
                selection = FavoriteNewsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FavoriteNewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                return updateArticle(uri, values, selection, selectionArgs);
            case NEWS_ID:
                selection = FavoriteNewsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateArticle(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateArticle(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(FavoriteNewsEntry.COLUMN_TITLE)) {
            String title = values.getAsString(FavoriteNewsEntry.COLUMN_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Title is required");
            }
        }
        if (values.containsKey(FavoriteNewsEntry.COLUMN_ARTICLE_URL)) {
            String articleUrl = values.getAsString(FavoriteNewsEntry.COLUMN_ARTICLE_URL);
            if (articleUrl == null) {
                throw new IllegalArgumentException("article URL is required");
            }
        }
        if (values.containsKey(FavoriteNewsEntry.COLUMN_API_URI)) {
            String apiUri = values.getAsString(FavoriteNewsEntry.COLUMN_API_URI);
            if (apiUri == null) {
                throw new IllegalArgumentException("API URI is required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = newsDataBaseHelper.getWritableDatabase();
        int rowsUpdated = database.update(FavoriteNewsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
