package com.sdzshn3.android.news247;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NewsActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<News>> {

    private static final String NEWSAPI_REQUEST_URL = "http://content.guardianapis.com/world/india";
    private static final String SEARCH_REQUEST_URL = "https://content.guardianapis.com/search?q=";
    private static final int NEWS_LOADER_ID = 1;
    private static final String apiKey = "api-key";
    private static final String showTags = "show-tags";
    private static final String contributorTag = "contributor";
    private static final String showFields = "show-fields";
    private static final String thumbnailField = "thumbnail";
    private static final String pageSize = "page-size";
    String mSearchQuery;
    boolean isConnected;
    private ArrayList<News> newsArray;
    @BindView(R.id.no_data_found)
    TextView mEmptyStateTextView;
    @BindView(R.id.list)
    RecyclerView newsRecyclerView;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.loading_circle)
    ProgressBar progressBar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.no_internet_connection)
    TextView noInternetConnectionTextView;
    private NewsAdapter mAdapter;
    android.support.v4.app.LoaderManager loaderManager;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ButterKnife.bind(this);

        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsArray.clear();
                mAdapter.notifyDataSetChanged();
                loaderManager.restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mTabLayout.addTab(mTabLayout.newTab().setText("All news").setIcon(R.drawable.ic_all));
        mTabLayout.addTab(mTabLayout.newTab().setText("Countries").setIcon(R.drawable.ic_place));
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);



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

    }

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    Toast.makeText(NewsActivity.this, "All news", Toast.LENGTH_SHORT).show();
                    return;
                case 1:
                    Toast.makeText(NewsActivity.this, "Countries", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @NonNull
    @Override
    public android.support.v4.content.Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
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
        Uri baseUri;
        if(mSearchQuery == null) {
            baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
            mTabLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            baseUri = Uri.parse(SEARCH_REQUEST_URL + mSearchQuery);
            mTabLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        Uri.Builder uriBuilder = baseUri.buildUpon();

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
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<List<News>> loader, List<News> newsList) {
        progressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        } else{
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
        }
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (newsList != null && !newsList.isEmpty()) {
            newsArray.addAll(newsList);
            mAdapter.notifyDataSetChanged();
            newsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setVisibility(View.GONE);
            noInternetConnectionTextView.setVisibility(View.GONE);
        } else {
            if(isConnected) {
                newsRecyclerView.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                noInternetConnectionTextView.setVisibility(View.GONE);
            } else {
                newsRecyclerView.setVisibility(View.GONE);
                mEmptyStateTextView.setVisibility(View.GONE);
                noInternetConnectionTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<News>> loader) {
        newsArray.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) NewsActivity.this.getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if(searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(NewsActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
                    loaderManager.restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            MenuItem item = menu.findItem(R.id.action_search);
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    mSearchQuery = null;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
                    loaderManager.restartLoader(NEWS_LOADER_ID, null, NewsActivity.this);
                    return true;
                }
            });
        }

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
        //newsArray.clear();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        newsArray.clear();
        mAdapter.notifyDataSetChanged();
    }
}
