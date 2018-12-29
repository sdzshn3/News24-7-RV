package com.sdzshn3.android.news247;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "telugu_news")
public class TeluguNewsModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String mTitle;
    private String mUrl;
    private String mPublishedAt;
    private String mThumbnail;

    public TeluguNewsModel(String mTitle, String mUrl, String mPublishedAt, String mThumbnail) {
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mPublishedAt = mPublishedAt;
        this.mThumbnail = mThumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

}