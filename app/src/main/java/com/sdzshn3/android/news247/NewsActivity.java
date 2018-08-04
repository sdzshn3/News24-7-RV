package com.sdzshn3.android.news247;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<News>> {

    private static final String NEWSAPI_REQUEST_URL = "http://content.guardianapis.com/world/india";
    private static final int NEWS_LOADER_ID = 1;
    boolean isConnected;
    ArrayList<News> newsArray;
    private TextView mEmptyStateTextView;
    private RecyclerView newsRecyclerView;
    private NewsAdapter mAdapter;
    private SwipeRefreshLayout mySwipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mEmptyStateTextView = findViewById(R.id.no_data_found);
        newsRecyclerView = findViewById(R.id.list);
        mySwipeRefreshLayout = findViewById(R.id.refresh_page);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        newsArray = new ArrayList<>();
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mySwipeRefreshLayout.setRefreshing(false);
                // TODO: 30/07/2018 refresh news articles
            }
        });

        if (newsArray.isEmpty()) {
            newsRecyclerView.setVisibility(View.GONE);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            newsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
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
                Boolean.parseBoolean(getString(R.string.default_show_author_name)));

        Uri baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key", BuildConfig.GUARDIAN_API_KEY);
        if (showAuthorName) {
            uriBuilder.appendQueryParameter("show-tags", "contributor");
        }
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("page-size", numberOfArticles);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        //mAdapter = null;
        clear();

        ProgressBar progressBar = findViewById(R.id.loading_circle);
        progressBar.setVisibility(View.GONE);

        if (!isConnected) {
            TextView textView = findViewById(R.id.no_internet_connection);
            textView.setText(R.string.no_internet_connection);
        } else {
            mEmptyStateTextView.setText(R.string.no_data_found);
        }

        if (newsList != null && !newsList.isEmpty()) {
            //this.mAdapter.addAll(newsList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void clear(){
        final int size = newsArray.size();
        if(size > 0){
            for(int i = 0; i <size; i++){
                newsArray.remove(0);
            }
            mAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //mAdapter = null;
        clear();
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
}
