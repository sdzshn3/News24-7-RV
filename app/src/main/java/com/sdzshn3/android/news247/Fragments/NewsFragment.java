package com.sdzshn3.android.news247.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.Adapters.NewsAdapter;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.SupportClasses.ItemClickSupport;
import com.sdzshn3.android.news247.SupportClasses.Utils;
import com.sdzshn3.android.news247.SupportClasses.WeatherIcon;
import com.sdzshn3.android.news247.ViewModel.NewsViewModel;
import com.sdzshn3.android.news247.ViewModel.NewsViewModelFactory;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import java.util.Objects;

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

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
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

    protected Context mContext;
    protected WeatherViewModel weatherViewModel;
    private NewsViewModel newsViewModel;
    private String mSearchQuery;
    private NewsAdapter newsAdapter;
    private String category;

    public NewsFragment() {
    }

    public static NewsFragment newInstance (String category) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        NewsFragment newsFragment = new NewsFragment();
        newsFragment.setArguments(bundle);
        return newsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list, container, false);
        mContext = getContext();
        setHasOptionsMenu(true);
        setRetainInstance(true);

        newsAdapter = new NewsAdapter();

        ButterKnife.bind(this, rootView);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        Utils.setUpRecyclerView(mContext, newsRecyclerView, mSwipeRefreshLayout);
        newsRecyclerView.setAdapter(newsAdapter);

        newsViewModel = ViewModelProviders.of(this, new NewsViewModelFactory(getActivity().getApplication(), category))
                .get(NewsViewModel.class);
        newsViewModel.articlePagedList.observe(this, articles -> {
            if (articles != null && !articles.isEmpty()) {
                newsAdapter.submitList(articles);
                mEmptyStateTextView.setVisibility(View.GONE);
            } else {
                if (Utils.isConnected(mContext)) {
                    if (mSearchQuery != null) {
                        newsAdapter.submitList(articles);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (mContext != null) {
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
                searchView.setOnQueryTextListener(this);
                searchItem.setOnActionExpandListener(this);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
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
            Objects.requireNonNull(newsViewModel.articlePagedList.getValue()).getDataSource().invalidate();
            weatherViewModel.refresh();
        } else {
            Snackbar.make(newsRecyclerView, "Internet connection not available", Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        Article currentArticle = newsAdapter.getCurrentList().get(position);
        Uri newsUri = Uri.parse(currentArticle.getUrl());
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        customTabsIntent.launchUrl(mContext, newsUri);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchQuery = query;
        /* TODO: update code to return results for search query */
        Objects.requireNonNull(newsViewModel.articlePagedList.getValue()).getDataSource().invalidate();
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
        /* TODO: update code to clear search query results */
        Objects.requireNonNull(newsViewModel.articlePagedList.getValue()).getDataSource().invalidate();
        return true;
    }
}
