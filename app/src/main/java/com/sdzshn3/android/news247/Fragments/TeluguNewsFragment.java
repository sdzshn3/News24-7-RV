package com.sdzshn3.android.news247.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.TeluguNewsAdapter;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.Utils;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.TeluguNewsModel;
import com.sdzshn3.android.news247.ViewModel.TeluguViewModel;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
            if (Utils.isConnected(mContext)) {
                TeluguRepository.loadData();
                weatherViewModel.refresh();
            } else {
                Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });

        Utils.setNoOfArticles(mContext);

        Utils.setUpRecyclerView(mContext, newsRecyclerView);
        newsRecyclerView.setAdapter(mAdapter);

        teluguViewModel = ViewModelProviders.of(TeluguNewsFragment.this).get(TeluguViewModel.class);
        teluguViewModel.getData().observe(TeluguNewsFragment.this, newsList -> {
            if (newsList != null && !newsList.isEmpty()) {
                mAdapter.submitList(newsList);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                if (Utils.isConnected(mContext)) {
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        });

        weatherViewModel = ViewModelProviders.of(TeluguNewsFragment.this).get(WeatherViewModel.class);
        weatherViewModel.getData().observe(TeluguNewsFragment.this, weatherModel -> {
            if (weatherModel != null) {
                String temp = String.valueOf(weatherModel.getMain().getTemp()).split("\\.", 2)[0];
                weatherTemp.setText(getString(R.string.weather_temperature_concatenate, temp, weatherModel.getName()));

                String iconId = weatherModel.getWeather().get(0).getIcon();
                weatherIcon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
            } else {
                if (Utils.isConnected(mContext)) {
                    weatherTemp.setText("unable to load");
                    weatherIcon.setImageResource(R.drawable.unknown);
                }
            }
        });

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            TeluguNewsModel currentNews = mAdapter.getItem(position);
            Uri newsUri = Uri.parse(currentNews.getArticleUrl());
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
            customTabsIntent.launchUrl(mContext, newsUri);
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }
}
