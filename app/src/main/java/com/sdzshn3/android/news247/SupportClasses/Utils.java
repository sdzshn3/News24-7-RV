package com.sdzshn3.android.news247.SupportClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.R;

import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Utils {

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        } else {
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
        }
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static String setUpUrl(Context context, String searchQuery, ProgressBar progressBar, String category) {
        String numberOfArticles = setNoOfArticles(context);

        Uri baseUri;
        if (searchQuery == null) {
            baseUri = Uri.parse(DataHolder.TOP_HEADLINES_REQUEST_URL);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            baseUri = Uri.parse(DataHolder.SEARCH_REQUEST_URL + searchQuery);
            progressBar.setVisibility(View.VISIBLE);
        }
        Uri.Builder uriBuilder = baseUri.buildUpon();
        if (category != null) {
            uriBuilder.appendQueryParameter(DataHolder.category, category);
        }
        uriBuilder.appendQueryParameter(DataHolder.apiKey, BuildConfig.NEWS_API_KEY);
        if (!numberOfArticles.isEmpty()) {
            if (Integer.parseInt(numberOfArticles) > 100) {
                numberOfArticles = "100";
            }
        }
        uriBuilder.appendQueryParameter(DataHolder.pageSize, numberOfArticles);
        return uriBuilder.toString();
    }

    public static String setNoOfArticles(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Resources resources = context.getResources();

        String numberOfArticles = sharedPrefs.getString(
                resources.getString(R.string.number_of_articles_key),
                resources.getString(R.string.default_no_of_news_articles)
        );

        if (numberOfArticles.trim().isEmpty()) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(resources.getString(R.string.number_of_articles_key), "10");
            editor.apply();
            numberOfArticles = sharedPrefs.getString(resources.getString(R.string.number_of_articles_key),
                    resources.getString(R.string.default_no_of_news_articles));
        }
        return numberOfArticles;
    }

    public static void setUpRecyclerView(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
    }
}
