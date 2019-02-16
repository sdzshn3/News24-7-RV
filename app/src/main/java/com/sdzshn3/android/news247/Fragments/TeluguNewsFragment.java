package com.sdzshn3.android.news247.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.sdzshn3.android.news247.Adapters.TeluguNewsAdapter;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.Utils;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.TeluguNewsModel;
import com.sdzshn3.android.news247.ViewModel.TeluguViewModel;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TeluguNewsFragment extends NewsFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_list)
    RecyclerView newsRecyclerView;
    @BindView(R.id.loading_circle)
    ProgressBar progressBar;
    @BindView(R.id.no_data_found)
    TextView mEmptyStateTextView;
    @BindView(R.id.weather_temp)
    TextView weatherTemp;
    @BindView(R.id.weather_icon)
    ImageView weatherIcon;

    public static String numberOfArticles;
    private Context mContext;
    private WeatherViewModel weatherViewModel;
    private TeluguViewModel teluguViewModel;
    private TeluguNewsAdapter mAdapter;

    public TeluguNewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        setHasOptionsMenu(true);
        mContext = getContext();

        mAdapter = new TeluguNewsAdapter();

        ButterKnife.bind(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        Utils.setNoOfArticles(mContext);

        Utils.setUpRecyclerView(mContext, newsRecyclerView, mSwipeRefreshLayout);
        newsRecyclerView.setAdapter(mAdapter);

        numberOfArticles = "30";
        TeluguViewModel.Factory factory = new TeluguViewModel.Factory(getActivity().getApplication());

        teluguViewModel = ViewModelProviders.of(TeluguNewsFragment.this, factory).get(TeluguViewModel.class);
        teluguViewModel.getmObservableTeluguNewsData().observe(TeluguNewsFragment.this, newsList -> {
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
                    weatherTemp.setText("Unable to load");
                    weatherIcon.setImageResource(R.drawable.unknown);
                }
            }
        });

        ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        teluguViewModel.getmObservableTeluguNewsData().observe(TeluguNewsFragment.this, newsList -> {
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
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        TeluguNewsModel teluguNewsModel = mAdapter.getItem(position);
        Uri newsUri = Uri.parse(teluguNewsModel.getUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        customTabsIntent.launchUrl(mContext, newsUri);
    }
}
