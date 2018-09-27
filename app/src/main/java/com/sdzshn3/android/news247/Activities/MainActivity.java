package com.sdzshn3.android.news247.Activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.sdzshn3.android.news247.Adapters.CategoryAdapter;
import com.sdzshn3.android.news247.Adapters.NewsFeedAdapter;
import com.sdzshn3.android.news247.Fragments.FavoritesFragment;
import com.sdzshn3.android.news247.Fragments.NewsFeedFragment;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NEWSAPI_REQUEST_URL = "http://content.guardianapis.com/world/india";
    private static final String SEARCH_REQUEST_URL = "https://content.guardianapis.com/search?q=";
    private static final String WEATHER_REQUEST_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final int NEWS_LOADER_ID = 1;
    private static final int WEATHER_LOADER_ID = 2;
    private static final String apiKey = "api-key";
    private static final String showTags = "show-tags";
    private static final String contributorTag = "contributor";
    private static final String showFields = "show-fields";
    private static final String thumbnailField = "thumbnail";
    private static final String pageSize = "page-size";
    String mSearchQuery;
    boolean isConnected;
    private ArrayList<News> newsArray;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private NewsFeedAdapter mAdapter;
    LinearLayoutManager layoutManager;
    android.support.v4.app.LoaderManager loaderManager;


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(categoryAdapter);
        mTabLayout.setupWithViewPager(viewPager);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //loaderManager = getSupportLoaderManager();
        //loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        //loaderManager.initLoader(WEATHER_LOADER_ID, null, this);
        //mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //@Override
            //public void onRefresh() {
                //newsArray.clear();
                //mAdapter.notifyDataSetChanged();
                //loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                //mSwipeRefreshLayout.setRefreshing(true);
            //}
        //});

        //newsArray = new ArrayList<>();
        //mAdapter = new NewsFeedAdapter(this, newsArray);

        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //newsRecyclerView.setLayoutManager(layoutManager);
        //newsRecyclerView.setHasFixedSize(true);
        //newsRecyclerView.setAdapter(mAdapter);

        //ItemClickSupport.addTo(newsRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            //@Override
            //public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //News currentNews = mAdapter.getItem(position);

                //Uri newsUri = Uri.parse(currentNews.getArticleUrl());

                //CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                //CustomTabsIntent customTabsIntent = builder.build();

                //builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                //customTabsIntent.launchUrl(getApplicationContext(), newsUri);
            //}
        //});


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        int id = menuItem.getItemId();
        switch (id){
            case R.id.news_feed_nav_item:
                CategoryAdapter categoryAdapter = new CategoryAdapter(this, getSupportFragmentManager());
                viewPager.setAdapter(categoryAdapter);
                mTabLayout.setupWithViewPager(viewPager);
                break;
            case R.id.favorites_nav_item:
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
                fragment = new FavoritesFragment();
        }
        drawer.closeDrawer(GravityCompat.START);

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
        }

        return true;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = null;
        if (searchItem != null) {
            searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    newsArray.clear();
                    mAdapter.notifyDataSetChanged();
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
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //to clear newsArray call this: newsArray.clear();
        //to notify the adapter that data set is changed, call this: mAdapter.notifyDataSetChanged();
    }*/
}
