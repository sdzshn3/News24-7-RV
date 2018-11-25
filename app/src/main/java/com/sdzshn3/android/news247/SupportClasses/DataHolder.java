package com.sdzshn3.android.news247.SupportClasses;

public final class DataHolder {
    public static final class holder {
        public static final int NEWS_LOADER_ID = 1;
        public static final int WEATHER_LOADER_ID = 2;
        public static final String NEWS_FEED_REQUEST_URL = "http://content.guardianapis.com/world/india";
        public static final String SCIENCE_NEWS_REQUEST_URL = "http://content.guardianapis.com/science";
        public static final String TECHNOLOGY_NEWS_REQUEST_URL = "http://content.guardianapis.com/uk/technology";
        public static final String BUSINESS_NEWS_REQUEST_URL = "http://content.guardianapis.com/uk/business";
        public static final String SEARCH_REQUEST_URL = "https://content.guardianapis.com/search?q=";
        public static final String WEATHER_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
        //remove -news if you want more articles
        public static final String TELUGU_NEWS_REQUEST_URL = "https://telugu.oneindia.com/rss/telugu-fb.xml";
        public static final String apiKey = "api-key";
        public static final String showTags = "show-tags";
        public static final String contributorTag = "contributor";
        public static final String showFields = "show-fields";
        public static final String thumbnailField = "thumbnail";
        public static final String bodyField = "body";
        public static final String page = "page";
        public static final int TOTAL_PAGES = 100;
        public static final String pageSize = "page-size";
        public static final String english = "english";
        public static final String telugu = "telugu";
        public static final String LANGUAGE_PREF_NAME = "language_selection";
        public static final String SELECTED_LANGUAGE = "selected_language";
        public static final int TELUGU_NEWS_LOADER_ID = 3;
    }
}
