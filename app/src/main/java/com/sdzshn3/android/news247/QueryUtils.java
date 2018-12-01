package com.sdzshn3.android.news247;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class QueryUtils {

    private String mTitle;
    private String mPubDate;
    private String mLink;
    private String mImageLink;

    private final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    public ArrayList<TeluguNewsModel> extractTeluguNewsFromRss(String requestUrl, int noOfArticles) {

        String title;
        String link;
        String publishedDate;
        String imageUrl;

        ArrayList<TeluguNewsModel> allNews = new ArrayList<>();
        try {
            URL url = new URL(requestUrl);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);

            XmlPullParser xpp = factory.newPullParser();

            try {
                xpp.setInput(getInputStream(url), "UTF_8");
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "No internet connection");
                return null;
            }

            boolean insideItem = false;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem) {
                            title = xpp.nextText();
                            if (title != null) {
                                mTitle = title;
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem) {
                            link = xpp.nextText();
                            if (link != null) {
                                mLink = link;
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("enclosure")) {
                        if (insideItem) {
                            imageUrl = xpp.getAttributeValue(null, "url");
                            if (imageUrl != null) {
                                mImageLink = imageUrl;
                            }
                        }
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (insideItem) {
                            publishedDate = xpp.nextText();
                            if (publishedDate != null) {
                                mPubDate = publishedDate;
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }
                try {
                    eventType = xpp.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (mTitle != null && mLink != null && mImageLink != null && mPubDate != null) {
                    TeluguNewsModel newsResult = new TeluguNewsModel(mTitle, mLink, mPubDate, mImageLink);
                    mTitle = null;
                    mLink = null;
                    mImageLink = null;
                    mPubDate = null;
                    allNews.add(newsResult);
                    if (allNews.size() == noOfArticles) {
                        return allNews;
                    }
                }
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Couldn't load new URL(request);");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Error in Pull parser");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with .next or .nextText");
            e.printStackTrace();
        }

        return allNews;
    }

    private static InputStream getInputStream(URL url) {
        HttpURLConnection urlConnection;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(20000/*Milliseconds*/);
            urlConnection.setConnectTimeout(30000/*Milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inputStream;
    }
}
