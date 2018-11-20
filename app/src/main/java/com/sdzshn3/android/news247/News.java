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
    private String mIconId;
    private static String mTemp;
    private String mContributorImage;
    private String mBodyHtml;

    public News(String sectionName, String title, String articleUrl, String apiUrl, String publishedAt, String firstName, String lastName, String thumbnail, String contributorImage, String bodyHtml) {
        mSectionName = sectionName;
        mTitle = title;
        mUrl = articleUrl;
        mApiUrl = apiUrl;
        mPublishedAt = publishedAt;
        mFirstName = firstName;
        mLastName = lastName;
        mThumbnail = thumbnail;
        mContributorImage = contributorImage;
        mBodyHtml = bodyHtml;
    }

    public News(String iconId, String temp) {
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

    public String getIconId() {
        return mIconId;
    }

    public static String getTemp() {
        return mTemp;
    }

    public String getContributorImage() {
        return mContributorImage;
    }

    public String getBodyHtml() {
        return mBodyHtml;
    }
}
