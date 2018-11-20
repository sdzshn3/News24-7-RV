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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.Activities.NewsDetailsActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.NewsFeedAdapter;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.SupportClasses.DataHolder.holder;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.NewsLoader;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScienceNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

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
    boolean gotWeather;

    public ScienceNewsFragment() {
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        mContext = getContext();
        setHasOptionsMenu(true);
        setRetainInstance(true);

        //Test app id "ca-app-pub-3940256099942544~3347511713"
        //This app's genuine id "ca-app-pub-4795017891549742~7471582838"
        MobileAds.initialize(mContext, "ca-app-pub-4795017891549742~7471582838");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("F5E71A24D5F33E8D35CFDF8B875D6E79").build();
        AdView adView = rootView.findViewById(R.id.banner_ad_news_feed);
        adView.loadAd(adRequest);

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
                loaderManager.restartLoader(holder.NEWS_LOADER_ID, null, ScienceNewsFragment.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);
        newsRecyclerView.setNestedScrollingEnabled(false);

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String currentPref = preferences.getString(getString(R.string.show_article_in_key), getString(R.string.default_show_as_plain));
                if(currentPref.equals(getString(R.string.default_show_as_plain))) {
                    News currentNews = mAdapter.getItem(position);
                    String bodyHtml = currentNews.getBodyHtml();
                    Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                    intent.setData(Uri.parse(bodyHtml));
                    startActivity(intent);
                } else {
                    News currentNews = mAdapter.getItem(position);
                    Uri newsUri = Uri.parse(currentNews.getArticleUrl());
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                    customTabsIntent.launchUrl(mContext, newsUri);
                }
            }
        });

        loaderManager = getLoaderManager();
        loaderManager.initLoader(holder.NEWS_LOADER_ID, null, ScienceNewsFragment.this);
        loaderManager.initLoader(holder.WEATHER_LOADER_ID, null, ScienceNewsFragment.this);

        return rootView;
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle bundle) {
        String url = null;
        switch (id) {
            case holder.NEWS_LOADER_ID:
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

                String numberOfArticles = sharedPrefs.getString(
                        getString(R.string.number_of_articles_key),
                        getString(R.string.default_no_of_news_articles)
                );

                if(numberOfArticles.trim().isEmpty()){
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(getString(R.string.number_of_articles_key), "10");
                    editor.apply();
                    numberOfArticles = sharedPrefs.getString(getString(R.string.number_of_articles_key),
                            getString(R.string.default_no_of_news_articles));
                }

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
                    baseUri = Uri.parse(holder.SCIENCE_NEWS_REQUEST_URL);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    baseUri = Uri.parse(holder.SEARCH_REQUEST_URL + mSearchQuery);
                    progressBar.setVisibility(View.VISIBLE);
                }
                Uri.Builder uriBuilder = baseUri.buildUpon();

                uriBuilder.appendQueryParameter(holder.apiKey, BuildConfig.GUARDIAN_API_KEY);
                if (showAuthorName) {
                    uriBuilder.appendQueryParameter(holder.showTags, holder.contributorTag);
                }
                if (showArticleImages) {
                    uriBuilder.appendQueryParameter(holder.showFields, holder.thumbnailField + "," + holder.bodyField);
                } else {
                    uriBuilder.appendQueryParameter(holder.showFields, holder.bodyField);
                }
                uriBuilder.appendQueryParameter(holder.pageSize, numberOfArticles);
                url = uriBuilder.toString();
                break;
            case holder.WEATHER_LOADER_ID:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                String weatherCity = sharedPreferences.getString(
                        getString(R.string.weather_city_key),
                        getString(R.string.weather_city_default)
                );
                url = holder.WEATHER_REQUEST_URL + weatherCity + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        }

        return new NewsLoader(mContext, id, url, 0);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {
        int id = loader.getId();
        switch (id) {
            case holder.NEWS_LOADER_ID:
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
                getLoaderManager().destroyLoader(holder.NEWS_LOADER_ID);
                return;
            case holder.WEATHER_LOADER_ID:
                if (newsList != null && !newsList.isEmpty()) {
                    final News news = newsList.get(0);
                    String temp = News.getTemp().split("\\.", 2)[0];
                    weatherTemp.setText(getString(R.string.weather_temperature_concatenate, temp));
                    gotWeather = true;
                    String iconId = news.getIconId();
                    weatherIcon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
                    getLoaderManager().destroyLoader(holder.WEATHER_LOADER_ID);
                }
        }
        if (!gotWeather) {
            gotWeather = false;
            weatherTemp.setText(R.string.weather_city_not_supported_text);
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
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
            }
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
                    loaderManager.restartLoader(holder.NEWS_LOADER_ID, null, ScienceNewsFragment.this);
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
                    loaderManager.restartLoader(holder.NEWS_LOADER_ID, null, ScienceNewsFragment.this);
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
        else if(id == R.id.action_change_language){
            startActivity(new Intent(mContext, LanguageSelectionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
