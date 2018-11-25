package com.sdzshn3.android.news247;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.sdzshn3.android.news247.SupportClasses.DataHolder.holder;

public class QueryUtils {

    private static final int readTimeout = 10000;
    private static final int connectTimeout = 15000;
    private String mTitle;
    private String mPubDate;
    private String mLink;
    private String mImageLink;

    private final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    private ArrayList<News> extractWeatherFromJson(String weatherJson) {
        if (TextUtils.isEmpty(weatherJson)) {
            return null;
        }
        ArrayList<News> weathers = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(weatherJson);
            int code = object.optInt("cod");
            if (code != 404) {
                JSONArray weather = object.getJSONArray("weather");
                JSONObject currentWeather = weather.getJSONObject(0);
                String iconId = currentWeather.optString("icon");
                JSONObject main = object.getJSONObject("main");
                String temp = main.optString("temp");

                News weatherResult = new News(iconId, temp);
                weathers.add(weatherResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weathers;
    }

    public List<News> fetchNewsData(int id, String requestUrl, int noOfArticles) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        switch (id) {
            case holder.NEWS_LOADER_ID:
                return extractFeatureFromJson(jsonResponse);
            case holder.WEATHER_LOADER_ID:
                return extractWeatherFromJson(jsonResponse);
            case holder.TELUGU_NEWS_LOADER_ID:
                return extractTeluguNewsFromRss(requestUrl, noOfArticles);
        }

        return extractFeatureFromJson(jsonResponse);
    }

    private ArrayList<News> extractTeluguNewsFromRss(String requestUrl, int noOfArticles) {


        String title;
        String link;
        String publishedDate;
        String imageUrl;

        ArrayList<News> allNews = new ArrayList<>();
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
                    News newsResult = new News(null, mTitle, mLink, null, mPubDate, null, mImageLink, null, null);
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
            //return url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inputStream;
    }

    private ArrayList<News> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        ArrayList<News> news = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            String status = response.getString("status");
            if (status.equals("ok")) {
                int pageSize = response.getInt("pageSize");
                JSONArray articlesArray = response.getJSONArray("results");

                for (int i = 0; i < pageSize; i++) {
                    JSONObject currentNewsArticle = articlesArray.getJSONObject(i);
                    String sectionName = currentNewsArticle.optString("sectionName");
                    String title = currentNewsArticle.optString("webTitle");
                    String articleUrl = currentNewsArticle.optString("webUrl");
                    String apiUrl = currentNewsArticle.optString("apiUrl");
                    apiUrl = apiUrl + "?api-key=" + BuildConfig.GUARDIAN_API_KEY;
                    String publishedAt = currentNewsArticle.optString("webPublicationDate");
                    String thumbnail = null;
                    String bodyHtml = null;
                    try {
                        JSONObject fields = currentNewsArticle.optJSONObject("fields");
                        thumbnail = fields.optString("thumbnail");
                        bodyHtml = fields.optString("body");
                    } catch (NullPointerException e) {
                        Log.e(LOG_TAG, "Images not found from JSON or field not requested in website");
                    }

                    String authorName = "";
                    String contributorImage = "";
                    try {
                        JSONArray tags = currentNewsArticle.getJSONArray("tags");
                        JSONObject currentTags = tags.getJSONObject(0);
                        authorName = currentTags.optString("webTitle");
                        contributorImage = currentTags.optString("bylineImageUrl");
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "No info found about author");
                    }

                    News newsResult = new News(sectionName, title, articleUrl, apiUrl, publishedAt, authorName, thumbnail, contributorImage, bodyHtml);
                    news.add(newsResult);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return news;
    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL");
        }
        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeout/*Milliseconds*/);
            urlConnection.setConnectTimeout(connectTimeout/*Milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON or RSS results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
