package com.sdzshn3.android.news247;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<News>> {

    private static final String NEWSAPI_REQUEST_URL = "http://content.guardianapis.com/world/india";
    private static final int NEWS_LOADER_ID = 1;
    private static final String apiKey = "api-key";
    private static final String showTags = "show-tags";
    private static final String contributorTag = "contributor";
    private static final String showFields = "show-fields";
    private static final String thumbnailField = "thumbnail";
    private static final String pageSize = "page-size";
    boolean isConnected;
    private ArrayList<News> newsArray;
    @BindView(R.id.no_data_found)
    TextView mEmptyStateTextView;
    @BindView(R.id.list)
    RecyclerView newsRecyclerView;
    private NewsAdapter mAdapter;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ButterKnife.bind(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        newsArray = new ArrayList<>();
        mAdapter = new NewsAdapter(this, newsArray);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                News currentNews = mAdapter.getItem(position);

                Uri newsUri = Uri.parse(currentNews.getArticleUrl());

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();

                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                customTabsIntent.launchUrl(getApplicationContext(), newsUri);
            }
        });

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String numberOfArticles = sharedPrefs.getString(
                getString(R.string.number_of_articles_key),
                getString(R.string.default_no_of_news_articles)
        );

        boolean showAuthorName = sharedPrefs.getBoolean(
                getString(R.string.author_name_key),
                Boolean.parseBoolean(getString(R.string.default_show_author_name))
        );

        boolean showArticleImages = sharedPrefs.getBoolean(
                getString(R.string.show_article_images_key),
                Boolean.parseBoolean(getString(R.string.default_show_article_images))
        );

        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        final Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(apiKey, BuildConfig.GUARDIAN_API_KEY);
        if (showAuthorName) {
            uriBuilder.appendQueryParameter(showTags, contributorTag);
        }
        if (showArticleImages) {
            uriBuilder.appendQueryParameter(showFields, thumbnailField);
        }
        uriBuilder.appendQueryParameter(pageSize, numberOfArticles);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {

        ProgressBar progressBar = findViewById(R.id.loading_circle);
        progressBar.setVisibility(View.GONE);

        if (!isConnected) {
            TextView textView = findViewById(R.id.no_internet_connection);
            textView.setText(R.string.no_internet_connection);
        } else {
            mEmptyStateTextView.setText(R.string.no_data_found);
        }

        if (newsList != null && !newsList.isEmpty()) {
            newsArray.addAll(newsList);
            mAdapter.notifyDataSetChanged();
            newsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);
        } else {
            newsRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsArray.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        newsArray.clear();
        mAdapter.notifyDataSetChanged();
    }
}
