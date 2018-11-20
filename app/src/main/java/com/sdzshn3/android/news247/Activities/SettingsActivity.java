package com.sdzshn3.android.news247.Activities;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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
                    if(!editTextPreference.getEditText().getText().toString().isEmpty()) {
                        if (Integer.parseInt(editTextPreference.getEditText().getText().toString()) > 100) {
                            editTextPreference.getEditText().setError("Maximum is 100");
                        } else if (Integer.parseInt(editTextPreference.getEditText().getText().toString()) == 0){
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

            final Preference showArticlesIn = findPreference(getString(R.string.show_article_in_key));
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String currentPref = preferences.getString(getString(R.string.show_article_in_key), getString(R.string.default_show_as_plain));
            if(currentPref.equals(getString(R.string.default_show_as_plain))) {
                showArticlesIn.setSummary(getString(R.string.show_as_plain_text_label));
            } else if (currentPref.equals(getString(R.string.show_in_browser))) {
                showArticlesIn.setSummary(getString(R.string.show_in_browser_label));
            }
            showArticlesIn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.radio_group_dialog, null);
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    Button button = dialogView.findViewById(R.id.done_button);
                    final RadioButton showAsPlainText = dialogView.findViewById(R.id.show_as_plain_text);
                    final RadioButton showInBrowser = dialogView.findViewById(R.id.show_in_browser);

                    final SharedPreferences.Editor editor = preferences.edit();
                    String currentPref = preferences.getString(getString(R.string.show_article_in_key), getString(R.string.default_show_as_plain));
                    if(currentPref.equals(getString(R.string.default_show_as_plain))) {
                        showAsPlainText.setChecked(true);
                        showInBrowser.setChecked(false);
                    } else if (currentPref.equals(getString(R.string.show_in_browser))) {
                        showInBrowser.setChecked(true);
                        showAsPlainText.setChecked(false);
                    }
                    final AlertDialog dialog = builder.create();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(showAsPlainText.isChecked()) {
                                editor.putString(preference.getKey(), getString(R.string.default_show_as_plain));
                                editor.apply();
                                showArticlesIn.setSummary(getString(R.string.show_as_plain_text_label));
                            } else if (showInBrowser.isChecked()) {
                                editor.putString(preference.getKey(), getString(R.string.show_in_browser));
                                editor.apply();
                                showArticlesIn.setSummary(getString(R.string.show_in_browser_label));
                            }
                            dialog.cancel();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });

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
                    if(isNumericOrCharacter || city.trim().isEmpty())
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
