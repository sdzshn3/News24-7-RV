package com.sdzshn3.android.news247;

public class News {
    private String mTitle;
    private String mUrl;
    private String mPublishedAt;
    private String mThumbnail;
    private String mIconId;
    private static String mTemp;

    public News(String title, String articleUrl, String publishedAt, String thumbnail) {
        mTitle = title;
        mUrl = articleUrl;
        mPublishedAt = publishedAt;
        mThumbnail = thumbnail;
    }

    public News(String iconId, String temp) {
        mIconId = iconId;
        mTemp = temp;
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

    public String getIconId() {
        return mIconId;
    }

    public static String getTemp() {
        return mTemp;
    }

}
