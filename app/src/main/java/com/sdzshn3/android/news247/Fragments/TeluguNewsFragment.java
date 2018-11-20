package com.sdzshn3.android.news247.Fragments;

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
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.NewsFeedAdapter;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.NewsLoader;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder.holder;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeluguNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    Context mContext;
    LoaderManager loaderManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView newsRecyclerView;
    LinearLayoutManager mLayoutManager;
    ProgressBar progressBar;
    boolean isConnected;
    TextView mEmptyStateTextView, noInternetConnectionTextView, weatherTemp;
    ImageView weatherIcon;
    private ArrayList<News> newsArray = new ArrayList<>();
    private NewsFeedAdapter mAdapter;
    boolean gotWeather;

    public TeluguNewsFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        setHasOptionsMenu(true);
        mContext = getContext();

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
                loaderManager.restartLoader(holder.TELUGU_NEWS_LOADER_ID, null, TeluguNewsFragment.this);
                loaderManager.restartLoader(holder.WEATHER_LOADER_ID, null, TeluguNewsFragment.this);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(mLayoutManager);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);
        newsRecyclerView.setNestedScrollingEnabled(false);

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
        loaderManager.initLoader(holder.WEATHER_LOADER_ID, null, TeluguNewsFragment.this);
        loaderManager.initLoader(holder.TELUGU_NEWS_LOADER_ID, null, TeluguNewsFragment.this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if(id == R.id.action_change_language){
            startActivity(new Intent(mContext, LanguageSelectionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle bundle) {
        String url = null;
        String numberOfArticles = "0";
        switch (id) {
            case holder.TELUGU_NEWS_LOADER_ID:

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

                numberOfArticles = sharedPrefs.getString(
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
                url = holder.TELUGU_NEWS_REQUEST_URL;
                break;
            case holder.WEATHER_LOADER_ID:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                String weatherCity = sharedPreferences.getString(
                        getString(R.string.weather_city_key),
                        getString(R.string.weather_city_default)
                );
                url = holder.WEATHER_REQUEST_URL + weatherCity + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        }
        return new NewsLoader(mContext, id, url, Integer.parseInt(numberOfArticles));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> newsList) {
        int id = loader.getId();
        switch (id) {
            case holder.TELUGU_NEWS_LOADER_ID:
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
                getLoaderManager().destroyLoader(holder.TELUGU_NEWS_LOADER_ID);
                break;
            case holder.WEATHER_LOADER_ID:
                if (newsList != null && !newsList.isEmpty()) {
                    final News news = newsList.get(0);
                    String temp = News.getTemp().split("\\.", 2)[0];
                    weatherTemp.setText(getString(R.string.weather_temperature_concatenate, temp));
                    gotWeather = true;
                    String iconId = news.getIconId();
                    //weatherIcon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
                    Picasso.get().load(WeatherIcon.getWeatherIcon(iconId)).resize(55, 55).into(weatherIcon);
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
}
