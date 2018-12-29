package com.sdzshn3.android.news247.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sdzshn3.android.news247.Fragments.NewsFragment;
import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;
import com.sdzshn3.android.news247.ViewModel.WeatherViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    androidx.appcompat.widget.Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private String newsLanguage;
    public static Activity activity;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static String city;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean detectCityAutomatically;
    SharedPreferences sharedPrefs;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity = MainActivity.this;

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        detectCityAutomatically = sharedPrefs.getBoolean(
                getString(R.string.detect_city_automatically_key),
                Boolean.valueOf(getString(R.string.detect_city_automatically_default))
        );

        if (detectCityAutomatically) {
            getLocation();
        }

        //Setting up the action bar and Navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Getting the language name from sharedPreferences.
        SharedPreferences sharedPreferences = this.getSharedPreferences(DataHolder.LANGUAGE_PREF_NAME, MODE_PRIVATE);
        newsLanguage = sharedPreferences.getString(DataHolder.SELECTED_LANGUAGE, DataHolder.english);
        //Setting the default Fragment when app launched
        if (savedInstanceState == null) {
            if (newsLanguage.equals(DataHolder.telugu)) {
                hideEnglishItems();
                setFragment(new TeluguNewsFragment());
            } else {
                setFragment(new NewsFragment());
            }
        }
    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Geocoder gcd = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses != null && addresses.size() > 0) {
                                city = addresses.get(0).getLocality();
                                WeatherViewModel weatherViewModel = new WeatherViewModel(getApplication());
                                weatherViewModel.refresh();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (fineLocation && coarseLocation) {
                if (detectCityAutomatically) {
                    getLocation();
                }
            } else {
                Snackbar.make(this.findViewById(android.R.id.content), "Please allow permission for weather or change in settings", Snackbar.LENGTH_LONG).setAction("ENABLE", v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }).show();
                Handler handler = new Handler();
                handler.postDelayed(() -> sharedPrefs.edit().putBoolean(getString(R.string.detect_city_automatically_key), false).apply(), 3500);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**If telugu language is selected, then hideEnglishItems will be called to hide English navMenus */
    private void hideEnglishItems(){
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.business_nav_item).setVisible(false).setEnabled(false).setChecked(false);
        navMenu.findItem(R.id.science_nav_item).setVisible(false).setEnabled(false).setChecked(false);
        navMenu.findItem(R.id.technology_nav_item).setVisible(false).setEnabled(false).setChecked(false);
        navMenu.findItem(R.id.health_nav_item).setVisible(false).setEnabled(false).setChecked(false);
        navMenu.findItem(R.id.entertainment_nav_item).setVisible(false).setEnabled(false).setChecked(false);
        navMenu.findItem(R.id.sports_nav_item).setVisible(false).setEnabled(false).setChecked(false);
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
                if(newsLanguage.equals(DataHolder.english)) {
                    setFragment(new NewsFragment());
                } else if (newsLanguage.equals(DataHolder.telugu)){
                    setFragment(new TeluguNewsFragment());
                }
                break;
            case R.id.science_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.science));
                break;
            case R.id.technology_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.technology));
                break;
            case R.id.business_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.business));
                break;
            case R.id.health_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.health));
                break;
            case R.id.entertainment_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.entertainment));
                break;
            case R.id.sports_nav_item:
                setFragment(NewsFragment.newInstance(DataHolder.sports));
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
                startActivity(Intent.createChooser(sendIntent, "Share app to"));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Setting fragment
    private void setFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            ft.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
    }
}