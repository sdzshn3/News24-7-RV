package com.sdzshn3.android.news247;

public class TeluguNewsModel {
    private String mTitle;
    private String mUrl;
    private String mPublishedAt;
    private String mThumbnail;

    public TeluguNewsModel(String title, String articleUrl, String publishedAt, String thumbnail) {
        mTitle = title;
        mUrl = articleUrl;
        mPublishedAt = publishedAt;
        mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArticleUrl() {
        return mUrl;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

}
