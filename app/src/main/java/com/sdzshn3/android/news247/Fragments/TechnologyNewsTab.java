package com.sdzshn3.android.news247.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.NewsFeedAdapter;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.ItemClickSupport;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.NewsLoader;
import com.sdzshn3.android.news247.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TechnologyNewsTab extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int NEWS_LOADER_ID = 1;
    private static final int WEATHER_LOADER_ID = 2;
    private static final String NEWSAPI_REQUEST_URL = "http://content.guardianapis.com/uk/technology";
    private static final String SEARCH_REQUEST_URL = "https://content.guardianapis.com/search?q=";
    private static final String WEATHER_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String apiKey = "api-key";
    private static final String showTags = "show-tags";
    private static final String contributorTag = "contributor";
    private static final String showFields = "show-fields";
    private static final String thumbnailField = "thumbnail";
    private static final String pageSize = "page-size";
    LoaderManager loaderManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView newsRecyclerView;
    LinearLayoutManager layoutManager;
    Context mContext;
    String mSearchQuery;
    ProgressBar progressBar;
    boolean isConnected;
    TextView mEmptyStateTextView, noInternetConnectionTextView, weatherTemp;
    ImageView weatherIcon;
    private ArrayList<News> newsArray = new ArrayList<>();
    private NewsFeedAdapter mAdapter;

    public TechnologyNewsTab() {
        //Required empty public constructor


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        mContext = getContext();
        setHasOptionsMenu(true);


        mAdapter = new NewsFeedAdapter(getActivity(), newsArray);

        newsRecyclerView = rootView.findViewById(R.id.recycler_view_list);
        progressBar = rootView.findViewById(R.id.loading_circle);
        mEmptyStateTextView = rootView.findViewById(R.id.no_data_found);
        noInternetConnectionTextView = rootView.findViewById(R.id.no_internet_connection);
        weatherTemp = rootView.findViewById(R.id.weather_temp);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsArray.clear();
                mAdapter.notifyDataSetChanged();
                loaderManager.restartLoader(NEWS_LOADER_ID, null, TechnologyNewsTab.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(layoutManager);
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
                customTabsIntent.launchUrl(mContext, newsUri);
            }
        });

        loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, TechnologyNewsTab.this);
        loaderManager.initLoader(WEATHER_LOADER_ID, null, TechnologyNewsTab.this);

        return rootView;
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle bundle) {

        String url = null;
        switch (id) {
            case NEWS_LOADER_ID:
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

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
                if (mSearchQuery == null) {
                    baseUri = Uri.parse(NEWSAPI_REQUEST_URL);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    baseUri = Uri.parse(SEARCH_REQUEST_URL + mSearchQuery);
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
                url = uriBuilder.toString();
                break;
            case WEATHER_LOADER_ID:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                String weatherCity = sharedPreferences.getString(
                        getString(R.string.weather_city_key),
                        getString(R.string.weather_city_default)
                );
                url = WEATHER_REQUEST_URL + weatherCity + "&units=metric&APPID=5798fa35ccb765dfe1658ef6a248eb23";
        }

        return new NewsLoader(mContext, id, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {
        int id = loader.getId();
        switch (id) {
            case NEWS_LOADER_ID:
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

                ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
                } else {
                    if (connectivityManager != null) {
                        activeNetwork = connectivityManager.getActiveNetworkInfo();
                    }
                }
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (newsList != null && !newsList.isEmpty()) {
                    newsArray.addAll(newsList);
                    newsRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    newsRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    noInternetConnectionTextView.setVisibility(View.GONE);
                } else {
                    if (isConnected) {
                        newsRecyclerView.setVisibility(View.GONE);
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        noInternetConnectionTextView.setVisibility(View.GONE);
                    } else {
                        newsRecyclerView.setVisibility(View.GONE);
                        mEmptyStateTextView.setVisibility(View.GONE);
                        noInternetConnectionTextView.setVisibility(View.VISIBLE);
                    }
                }
                getLoaderManager().destroyLoader(NEWS_LOADER_ID);
                return;
            case WEATHER_LOADER_ID:
                if (newsList != null && !newsList.isEmpty()) {
                    final News news = newsList.get(0);
                    String temp = news.getTemp().split("\\.", 2)[0];
                    weatherTemp.setText(temp + " ℃");

                    String iconId = news.getIconId();
                    switch (iconId) {
                        case "11d":
                            weatherIcon.setImageResource(R.drawable.thunder_day);
                            break;
                        case "11n":
                            weatherIcon.setImageResource(R.drawable.thunder_night);
                            break;
                        case "09d":
                            weatherIcon.setImageResource(R.drawable.rainy_weather);
                            break;
                        case "09n":
                            weatherIcon.setImageResource(R.drawable.rainy_night);
                            break;
                        case "10d":
                            weatherIcon.setImageResource(R.drawable.rainy_day);
                            break;
                        case "10n":
                            weatherIcon.setImageResource(R.drawable.rainy_night);
                            break;
                        case "13d":
                            weatherIcon.setImageResource(R.drawable.rain_snow);
                            break;
                        case "13n":
                            weatherIcon.setImageResource(R.drawable.rain_snow_night);
                            break;
                        case "50d":
                            weatherIcon.setImageResource(R.drawable.haze_day);
                            break;
                        case "50n":
                            weatherIcon.setImageResource(R.drawable.haze_night);
                            break;
                        case "01d":
                            weatherIcon.setImageResource(R.drawable.clear_day);
                            break;
                        case "01n":
                            weatherIcon.setImageResource(R.drawable.clear_night);
                            break;
                        case "02d":
                            weatherIcon.setImageResource(R.drawable.partly_cloudy);
                            break;
                        case "02n":
                            weatherIcon.setImageResource(R.drawable.partly_cloudy_night);
                            break;
                        case "03d":
                            weatherIcon.setImageResource(R.drawable.cloudy_weather);
                            break;
                        case "03n":
                            weatherIcon.setImageResource(R.drawable.cloudy_weather);
                            break;
                        case "04d":
                            weatherIcon.setImageResource(R.drawable.mostly_cloudy);
                            break;
                        case "04n":
                            weatherIcon.setImageResource(R.drawable.mostly_cloudy_night);
                            break;
                        default:
                            weatherIcon.setImageResource(R.drawable.unknown);

                    }
                    getLoaderManager().destroyLoader(WEATHER_LOADER_ID);
                }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        newsArray.clear();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            MainActivity mainActivity = new MainActivity();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
                    loaderManager.restartLoader(NEWS_LOADER_ID, null, TechnologyNewsTab.this);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    mSearchQuery = null;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
                    loaderManager.restartLoader(NEWS_LOADER_ID, null, TechnologyNewsTab.this);
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
