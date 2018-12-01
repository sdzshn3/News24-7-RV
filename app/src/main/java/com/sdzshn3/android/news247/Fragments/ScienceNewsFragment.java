package com.sdzshn3.android.news247.Fragments;

import android.app.SearchManager;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.ArticleAdapter;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.ViewModel.ScienceViewModel;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import java.util.Objects;

public class ScienceNewsFragment extends Fragment {

    public static String URL;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView newsRecyclerView;
    private Context mContext;
    private String mSearchQuery;
    private ProgressBar progressBar;
    private TextView mEmptyStateTextView, weatherTemp;
    private ImageView weatherIcon;
    private ScienceViewModel scienceViewModel;
    private WeatherViewModel weatherViewModel;
    private ArticleAdapter mAdapter;

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

        mAdapter = new ArticleAdapter();

        newsRecyclerView = rootView.findViewById(R.id.recycler_view_list);
        progressBar = rootView.findViewById(R.id.loading_circle);
        mEmptyStateTextView = rootView.findViewById(R.id.no_data_found);
        weatherTemp = rootView.findViewById(R.id.weather_temp);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (isConnected()) {
                scienceViewModel.refresh();
                weatherViewModel.refresh();
            } else {
                Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        setUpUrl();

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);
        newsRecyclerView.setNestedScrollingEnabled(false);

        scienceViewModel = ViewModelProviders.of(ScienceNewsFragment.this).get(ScienceViewModel.class);
        scienceViewModel.getData().observe(ScienceNewsFragment.this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                mAdapter.submitList(articles);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                if (isConnected()) {
                    if (mSearchQuery != null) {
                        mAdapter.submitList(articles);
                    }
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        });

        weatherViewModel = ViewModelProviders.of(ScienceNewsFragment.this).get(WeatherViewModel.class);
        weatherViewModel.getData().observe(ScienceNewsFragment.this, weatherModel -> {
            if (weatherModel != null) {
                String temp = String.valueOf(weatherModel.getMain().getTemp()).split("\\.", 2)[0];
                weatherTemp.setText(getString(R.string.weather_temperature_concatenate, temp, weatherModel.getName()));

                String iconId = weatherModel.getWeather().get(0).getIcon();
                weatherIcon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
            } else {
                if (isConnected()) {
                    weatherTemp.setText("Unable to load");
                    weatherIcon.setImageResource(R.drawable.unknown);
                }
            }
        });

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            Article currentArticle = mAdapter.getItem(position);
            Uri newsUri = Uri.parse(currentArticle.getUrl());
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
            customTabsIntent.launchUrl(mContext, newsUri);
        });
        return rootView;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            activeNetwork = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        } else {
            if (connectivityManager != null) {
                activeNetwork = connectivityManager.getActiveNetworkInfo();
            }
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void setUpUrl() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        String numberOfArticles = sharedPrefs.getString(
                getString(R.string.number_of_articles_key),
                getString(R.string.default_no_of_news_articles)
        );

        if (numberOfArticles.trim().isEmpty()) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(getString(R.string.number_of_articles_key), "10");
            editor.apply();
            numberOfArticles = sharedPrefs.getString(getString(R.string.number_of_articles_key),
                    getString(R.string.default_no_of_news_articles));
        }

        Uri baseUri;
        if (mSearchQuery == null) {
            baseUri = Uri.parse(DataHolder.TOP_HEADLINES_REQUEST_URL);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            baseUri = Uri.parse(DataHolder.SEARCH_REQUEST_URL + mSearchQuery);
            progressBar.setVisibility(View.VISIBLE);
        }
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(DataHolder.category, DataHolder.science);
        uriBuilder.appendQueryParameter(DataHolder.apiKey, BuildConfig.NEWS_API_KEY);
        if (!numberOfArticles.isEmpty()) {
            if (Integer.parseInt(numberOfArticles) > 100) {
                numberOfArticles = "100";
            }
        }
        uriBuilder.appendQueryParameter(DataHolder.pageSize, numberOfArticles);
        URL = uriBuilder.toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        androidx.appcompat.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            MainActivity mainActivity = new MainActivity();
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivity.getComponentName()));
            }
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    setUpUrl();
                    scienceViewModel.refresh();
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
                    setUpUrl();
                    scienceViewModel.refresh();
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
        } else if (id == R.id.action_change_language) {
            startActivity(new Intent(mContext, LanguageSelectionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
