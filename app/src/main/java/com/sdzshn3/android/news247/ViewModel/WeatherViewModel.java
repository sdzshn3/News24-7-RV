package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.QueryUtils;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    private static MutableLiveData<List<News>> data = new MutableLiveData<>();

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        loadData();
    }

    public LiveData<List<News>> getData() {
        return data;
    }

    public static void loadData() {
        new AsyncTask<Void, Void, List<News>>() {
            @Override
            protected List<News> doInBackground(Void... voids) {
                QueryUtils queryUtils = new QueryUtils();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
                String weatherCity = sharedPreferences.getString(
                        MainActivity.activity.getString(R.string.weather_city_key),
                        MainActivity.activity.getString(R.string.weather_city_default)
                );
                String url = DataHolder.WEATHER_REQUEST_URL + weatherCity + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
                return queryUtils.fetchNewsData(DataHolder.WEATHER_LOADER_ID, url, 0);
            }

            @Override
            protected void onPostExecute(List<News> newsList) {
                data.postValue(newsList);
            }
        }.execute();
    }
}
