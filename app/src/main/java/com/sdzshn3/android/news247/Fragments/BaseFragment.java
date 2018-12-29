package com.sdzshn3.android.news247.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.ArticleAdapter;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.Utils;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.ViewModel.BaseViewModel;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ItemClickSupport.OnItemClickListener, SearchView.OnQueryTextListener,MenuItem.OnActionExpandListener {

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

    public static String URL;
    protected Context mContext;
    protected WeatherViewModel weatherViewModel;
    private BaseViewModel baseViewModel;
    private String mSearchQuery;
    private ArticleAdapter mAdapter;
    private String category;

    public BaseFragment() {
    }

    public static BaseFragment newInstance (String category) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        BaseFragment baseFragment = new BaseFragment();
        baseFragment.setArguments(bundle);
        return baseFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        mContext = getContext();
        setHasOptionsMenu(true);
        setRetainInstance(true);

        mAdapter = new ArticleAdapter();

        ButterKnife.bind(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        URL = Utils.setUpUrl(mContext, mSearchQuery, progressBar, category);

        Utils.setUpRecyclerView(mContext, newsRecyclerView);
        newsRecyclerView.setAdapter(mAdapter);

        baseViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        baseViewModel.getData().observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                mAdapter.submitList(articles);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                if (Utils.isConnected(mContext)) {
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

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        weatherViewModel.getData().observe(this, weatherModel -> {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_change_language) {
            startActivity(new Intent(getContext(), LanguageSelectionActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (Utils.isConnected(mContext)) {
            baseViewModel.refresh();
            weatherViewModel.refresh();
        } else {
            Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        Article currentArticle = mAdapter.getItem(position);
        Uri newsUri = Uri.parse(currentArticle.getUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        customTabsIntent.launchUrl(mContext, newsUri);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchQuery = query;
        Utils.setUpUrl(mContext, mSearchQuery, progressBar, category);
        baseViewModel.refresh();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mSearchQuery = null;
        Utils.setUpUrl(mContext, null, progressBar, category);
        baseViewModel.refresh();
        return true;
    }
}
