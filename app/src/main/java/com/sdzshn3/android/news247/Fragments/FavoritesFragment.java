package com.sdzshn3.android.news247.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.FavoritesAdapter;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.NewsLoader;
import com.sdzshn3.android.news247.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "news247-favorites";
    private static final String WEATHER_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final int FAVORITES_LOADER_ID = 3;
    private static final int WEATHER_LOADER_ID = 4;
    private ArrayList<News> newsArray = new ArrayList<>();
    private FavoritesAdapter mAdapter;
    private String articleUrl;
    LoaderManager loaderManager;
    RecyclerView newsRecyclerView;
    ProgressBar progressBar;
    TextView mEmptyStateTextView, noInternetConnectionTextView, weatherTemp;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView weatherIcon;
    LinearLayoutManager layoutManager;

    public FavoritesFragment() {
        //Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        setHasOptionsMenu(true);
        mContext = getContext();

        FavoritesFragment favoritesFragment = new FavoritesFragment();
        favoritesFragment.setRetainInstance(true);

        mAdapter = new FavoritesAdapter(getActivity(), newsArray);

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
                startLoading(false);
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);

        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        loaderManager = getLoaderManager();

        startLoading(true);


        return rootView;
    }

    private void startLoading(boolean initializeOrRestart) {
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            articleUrl = entry.getKey();

            if (initializeOrRestart) {
                loaderManager.restartLoader(FAVORITES_LOADER_ID, null, FavoritesFragment.this);
            } else {
                mAdapter.notifyDataSetChanged();
                loaderManager.restartLoader(FAVORITES_LOADER_ID, null, FavoritesFragment.this);
            }
        }

        if (initializeOrRestart) {
            loaderManager.restartLoader(WEATHER_LOADER_ID, null, FavoritesFragment.this);
        } else {
            newsArray.clear();
            loaderManager.restartLoader(WEATHER_LOADER_ID, null, FavoritesFragment.this);
        }
        if (articleUrl == null) {
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            if (isConnected()) {
                mEmptyStateTextView.setVisibility(View.VISIBLE);
                noInternetConnectionTextView.setVisibility(View.GONE);
            } else {
                mEmptyStateTextView.setVisibility(View.GONE);
                noInternetConnectionTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
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

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle bundle) {
        String url = null;
        if (id == FAVORITES_LOADER_ID) {
            url = articleUrl;
        }
        if (id == WEATHER_LOADER_ID) {

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
            case FAVORITES_LOADER_ID:
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

                if (newsList != null && !newsList.isEmpty()) {
                    notifyDataChanged(newsList);
                    newsRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    noInternetConnectionTextView.setVisibility(View.GONE);
                } else {
                    if (isConnected()) {
                        newsRecyclerView.setVisibility(View.GONE);
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        noInternetConnectionTextView.setVisibility(View.GONE);
                    } else {
                        newsRecyclerView.setVisibility(View.GONE);
                        mEmptyStateTextView.setVisibility(View.GONE);
                        noInternetConnectionTextView.setVisibility(View.VISIBLE);
                    }
                }
                getLoaderManager().destroyLoader(FAVORITES_LOADER_ID);
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

    private void notifyDataChanged(List<News> newsList) {
        newsArray.addAll(newsList);
        newsRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        mAdapter.notifyDataSetChanged();
    }
}
