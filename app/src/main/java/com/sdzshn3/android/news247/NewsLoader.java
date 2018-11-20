package com.sdzshn3.android.news247;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

public class NewsLoader extends android.support.v4.content.AsyncTaskLoader<List<News>> {

    private String mUrl;
    private int mId;
    private int mNoIfArticles;

    public NewsLoader(Context context, int id, String url, @Nullable int noOfArticles) {
        super(context);
        mUrl = url;
        mId = id;
        mNoIfArticles = noOfArticles;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        QueryUtils queryUtils = new QueryUtils();
        return queryUtils.fetchNewsData(mId, mUrl, mNoIfArticles);
    }
}
