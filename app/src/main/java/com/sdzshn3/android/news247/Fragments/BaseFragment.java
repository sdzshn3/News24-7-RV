package com.sdzshn3.android.news247.Fragments;

import android.content.Intent;
import android.view.MenuItem;

import com.sdzshn3.android.news247.Activities.LanguageSelectionActivity;
import com.sdzshn3.android.news247.Activities.SettingsActivity;
import com.sdzshn3.android.news247.R;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

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
}
