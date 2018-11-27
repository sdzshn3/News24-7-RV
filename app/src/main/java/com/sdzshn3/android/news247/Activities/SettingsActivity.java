package com.sdzshn3.android.news247.Activities;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.sdzshn3.android.news247.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            final EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.number_of_articles_key));
            editTextPreference.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!editTextPreference.getEditText().getText().toString().isEmpty()) {
                        if (Integer.parseInt(editTextPreference.getEditText().getText().toString()) > 100) {
                            editTextPreference.getEditText().setError("Maximum is 100");
                        } else if (Integer.parseInt(editTextPreference.getEditText().getText().toString()) == 0) {
                            editTextPreference.getEditText().setError("Minimum is 1");
                        }
                    } else {
                        editTextPreference.getEditText().setError("Invalid number");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            Preference numberOfArticles = findPreference(getString(R.string.number_of_articles_key));
            bindPreferenceSummaryToValue(numberOfArticles);

            final EditTextPreference weatherCityEditTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(getString(R.string.weather_city_key));
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
