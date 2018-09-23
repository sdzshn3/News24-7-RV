package com.sdzshn3.android.news247;

import android.content.Context;
import java.util.List;

public class NewsLoader extends android.support.v4.content.AsyncTaskLoader<List<News>> {

    private String mUrl;

    NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
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
        return QueryUtils.fetchNewsData(mUrl);
    }
}
