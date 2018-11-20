package com.sdzshn3.android.news247.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sdzshn3.android.news247.Fragments.BusinessNewsFragment;
import com.sdzshn3.android.news247.Fragments.NewsFeedFragment;
import com.sdzshn3.android.news247.Fragments.ScienceNewsFragment;
import com.sdzshn3.android.news247.Fragments.TechnologyNewsFragment;
import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder.holder;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    String newsLanguage;
    public static Activity activity;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity = MainActivity.this;


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = this.getSharedPreferences(holder.LANGUAGE_PREF_NAME, MODE_PRIVATE);
        newsLanguage = sharedPreferences.getString(holder.SELECTED_LANGUAGE, holder.english);
        if(newsLanguage.equals(holder.telugu)){
            hideEnglishItems();
            setFragment(new TeluguNewsFragment());
        } else {
            //Setting the default Fragment when app launched
            setFragment(new NewsFeedFragment());
        }
    }

    private void hideEnglishItems(){
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.business_nav_item).setVisible(false);
        navMenu.findItem(R.id.science_nav_item).setVisible(false);
        navMenu.findItem(R.id.technology_nav_item).setVisible(false);
    }

    @Override
    public void onBackPressed() {
        //Closing navigation drawer if it is open
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.news_feed_nav_item:
                if(newsLanguage.equals(holder.english)) {
                    setFragment(new NewsFeedFragment());
                } else if (newsLanguage.equals(holder.telugu)){
                    setFragment(new TeluguNewsFragment());
                }
                break;
            case R.id.science_nav_item:
                setFragment(new ScienceNewsFragment());
                break;
            case R.id.technology_nav_item:
                setFragment(new TechnologyNewsFragment());
                break;
            case R.id.business_nav_item:
                setFragment(new BusinessNewsFragment());
                break;
            case R.id.settings_nav_item:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.change_language_nav_item:
                startActivity(new Intent(MainActivity.this, LanguageSelectionActivity.class));
                break;
            case R.id.share_app_nav_item:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sdzshn3.android.news247");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share app with"));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //setting fragment
    public void setFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}