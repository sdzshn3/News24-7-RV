package com.sdzshn3.android.news247.Fragments;

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
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.TeluguNewsAdapter;
import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.TeluguNewsModel;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.ViewModel.TeluguViewModel;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import java.util.Objects;

public class TeluguNewsFragment extends Fragment {

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView newsRecyclerView;
    private ProgressBar progressBar;
    private TextView mEmptyStateTextView, weatherTemp;
    private ImageView weatherIcon;
    private TeluguNewsAdapter mAdapter;
    public static String numberOfArticles;
    private WeatherViewModel weatherViewModel;
    private TeluguViewModel teluguViewModel;

    public TeluguNewsFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        setHasOptionsMenu(true);
        mContext = getContext();

        mAdapter = new TeluguNewsAdapter();

        newsRecyclerView = rootView.findViewById(R.id.recycler_view_list);
        progressBar = rootView.findViewById(R.id.loading_circle);
        mEmptyStateTextView = rootView.findViewById(R.id.no_data_found);
        weatherTemp = rootView.findViewById(R.id.weather_temp);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (isConnected()) {

                weatherViewModel.refresh();
            } else {
                Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });

        setNoOfArticles();

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setAdapter(mAdapter);
        newsRecyclerView.setNestedScrollingEnabled(false);

        TeluguViewModel.Factory factory = new TeluguViewModel.Factory(getActivity().getApplication());

        teluguViewModel = ViewModelProviders.of(TeluguNewsFragment.this, factory).get(TeluguViewModel.class);
        /*teluguViewModel.getData().observe(TeluguNewsFragment.this, newsList -> {
            if (newsList != null && !newsList.isEmpty()) {
                mAdapter.submitList(newsList);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                if (isConnected()) {
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        });*/
        teluguViewModel.getmObservableTeluguNewsData().observe(TeluguNewsFragment.this, newsList -> {
            mAdapter.submitList(newsList);
        });

        weatherViewModel = ViewModelProviders.of(TeluguNewsFragment.this).get(WeatherViewModel.class);
        weatherViewModel.getData().observe(TeluguNewsFragment.this, weatherModel -> {
            if (weatherModel != null) {
                String temp = String.valueOf(weatherModel.getMain().getTemp()).split("\\.", 2)[0];
                weatherTemp.setText(getString(R.string.weather_temperature_concatenate, temp, weatherModel.getName()));

                String iconId = weatherModel.getWeather().get(0).getIcon();
                weatherIcon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
            } else {
                if (isConnected()) {
                    weatherTemp.setText("unable to load");
                    weatherIcon.setImageResource(R.drawable.unknown);
                }
            }
        });

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            TeluguNewsModel currentNews = mAdapter.getItem(position);
            Uri newsUri = Uri.parse(currentNews.getUrl());
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

    private void setNoOfArticles() {
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

}
