package com.sdzshn3.android.news247;

public class News {
    private String mSectionName;
    private String mTitle;
    private String mUrl;
    private String mApiUrl;
    private String mPublishedAt;
    private String mFirstName;
    private String mLastName;
    private String mThumbnail;
    private String mWeatherId, mWeatherDesc, mIconId, mTemp;

    public News(String sectionName, String title, String articleUrl, String apiUrl, String publishedAt, String firstName, String lastName, String thumbnail) {
        mSectionName = sectionName;
        mTitle = title;
        mUrl = articleUrl;
        mApiUrl = apiUrl;
        mPublishedAt = publishedAt;
        mFirstName = firstName;
        mLastName = lastName;
        mThumbnail = thumbnail;
    }

    public News(String weatherId, String weatherDesc, String iconId, String temp){
        mWeatherId = weatherId;
        mWeatherDesc = weatherDesc;
        mIconId = iconId;
        mTemp = temp;
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

    public String getApiUrl(){
        return mApiUrl;
    }

    public String getPublishedAt() {
        return mPublishedAt;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getWeatherId(){
        return mWeatherId;
    }

    public String getWeatherDesc(){
        return mWeatherDesc;
    }

    public String getIconId(){
        return mIconId;
    }

    public String getTemp(){
        return mTemp;
    }
}
