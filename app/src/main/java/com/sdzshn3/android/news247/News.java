package com.sdzshn3.android.news247;

public class News {
    private String mSectionName;
    private String mTitle;
    private String mUrl;
    private String mPublishedAt;
    private String mFirstName;
    private String mLastName;
    private String mThumbnail;

    public News(String sectionName, String title, String articleUrl, String publishedAt, String firstName, String lastName, String thumbnail) {
        mSectionName = sectionName;
        mTitle = title;
        mUrl = articleUrl;
        mPublishedAt = publishedAt;
        mFirstName = firstName;
        mLastName = lastName;
        mThumbnail = thumbnail;
    }

    public String getSectionName() {
        return mSectionName;
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

    public String getFirstName() { return mFirstName; }

    public String getLastName() { return mLastName; }

    public String getThumbnail() { return mThumbnail; }
}
