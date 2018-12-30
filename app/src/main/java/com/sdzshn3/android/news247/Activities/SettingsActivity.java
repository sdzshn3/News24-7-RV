package com.sdzshn3.android.news247.Activities;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.snackbar.Snackbar;
import com.sdzshn3.android.news247.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;


public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private EditTextPreference noOfArticlesToLoad;
        private EditTextPreference weatherCityEditTextPreference;
        private SwitchPreference detectCityAutomatically;
        private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            noOfArticlesToLoad = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.number_of_articles_key));
            weatherCityEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.weather_city_key));
            detectCityAutomatically = (SwitchPreference) getPreferenceScreen().findPreference(getString(R.string.detect_city_automatically_key));

            noOfArticlesToLoad.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!noOfArticlesToLoad.getEditText().getText().toString().isEmpty()) {
                        if (Integer.parseInt(noOfArticlesToLoad.getEditText().getText().toString()) > 100) {
                            noOfArticlesToLoad.getEditText().setError("Maximum is 100");
                        } else if (Integer.parseInt(noOfArticlesToLoad.getEditText().getText().toString()) == 0) {
                            noOfArticlesToLoad.getEditText().setError("Minimum is 1");
                        }
                    } else {
                        noOfArticlesToLoad.getEditText().setError("Invalid number");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            Preference numberOfArticles = findPreference(getString(R.string.number_of_articles_key));
            bindPreferenceSummaryToValue(numberOfArticles);

            if (!detectCityAutomatically.isChecked()) {
                weatherCityEditTextPreference.setEnabled(true);
                weatherCityEditTextPreference.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String city = weatherCityEditTextPreference.getEditText().getText().toString();
                        Pattern p = Pattern.compile("[^a-z]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(city);
                        boolean isNumericOrCharacter = m.find();
                        if (isNumericOrCharacter || city.trim().isEmpty())
                            weatherCityEditTextPreference.getEditText().setError("Invalid city");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                Preference weatherCity = findPreference(getString(R.string.weather_city_key));
                bindPreferenceSummaryToValue(weatherCity);
            } else {
                weatherCityEditTextPreference.setEnabled(false);
                Preference weatherCity = findPreference(getString(R.string.weather_city_key));
                bindPreferenceSummaryToValue(weatherCity);
            }


            detectCityAutomatically.setOnPreferenceChangeListener((preference, newValue) -> {
                switch (newValue.toString()) {
                    case "true":
                        weatherCityEditTextPreference.setEnabled(false);
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        }
                        break;
                    case "false":
                        weatherCityEditTextPreference.setEnabled(true);
                }
                return true;
            });
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                boolean coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!fineLocation && !coarseLocation) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Couldn't able to get city name due to location permission denied", Snackbar.LENGTH_LONG).setAction("ENABLE", v -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    }).show();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        detectCityAutomatically.setChecked(false);
                        weatherCityEditTextPreference.setEnabled(true);
                    }, 3500);
                }
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
