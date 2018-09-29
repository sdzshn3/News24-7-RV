package com.sdzshn3.android.news247.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class FavoriteNewsContract {

    public static final String CONTENT_AUTHORITY = "com.sdzshn3.android.news247";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "favorites";

    private FavoriteNewsContract() {

    }

    public static final class FavoriteNewsEntry implements BaseColumns {
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);

        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SECTION_NAME = "sectionName";
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_ARTICLE_URL = "articleUri";
        public final static String COLUMN_API_URI = "apiUri";
        public final static String COLUMN_PUBLISHED_AT = "publishedAt";
        public final static String COLUMN_FIRST_NAME = "firstName";
        public final static String COLUMN_LAST_NAME = "lastName";
        public final static String COLUMN_THUMBNAIL = "thumbnail";
    }
}
