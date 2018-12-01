package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sdzshn3.android.news247.Activities.MainActivity;
import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Weather.WeatherClient;
import com.sdzshn3.android.news247.Retrofit.Weather.WeatherModel;

public class WeatherViewModel extends AndroidViewModel {
    private static MutableLiveData<WeatherModel> data = new MutableLiveData<>();

    private ApiService apiService;
    private static Call<WeatherModel> call;
    public WeatherViewModel(@NonNull Application application) {
        super(application);
        apiService = WeatherClient.getApiService();
        loadData();
    }

    public LiveData<WeatherModel> getData() {
        return data;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String weatherCity = sharedPreferences.getString(
                MainActivity.activity.getString(R.string.weather_city_key),
                MainActivity.activity.getString(R.string.weather_city_default)
        );

        boolean detectCityAutomatically = sharedPreferences.getBoolean(
                MainActivity.activity.getString(R.string.detect_city_automatically_key),
                Boolean.parseBoolean(MainActivity.activity.getString(R.string.detect_city_automatically_default))
        );
        String city;
        if (detectCityAutomatically) {
            city = MainActivity.city;
        } else {
            city = weatherCity;
        }

        String url = "weather?q=" + city + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        call = apiService.getWeatherData(url);
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.e("WeatherViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public void refresh() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String weatherCity = sharedPreferences.getString(
                MainActivity.activity.getString(R.string.weather_city_key),
                MainActivity.activity.getString(R.string.weather_city_default)
        );
        boolean detectCityAutomatically = sharedPreferences.getBoolean(
                MainActivity.activity.getString(R.string.detect_city_automatically_key),
                Boolean.parseBoolean(MainActivity.activity.getString(R.string.detect_city_automatically_default))
        );
        String city;
        if (detectCityAutomatically) {
            city = MainActivity.city;
        } else {
            city = weatherCity;
        }
        String url = "weather?q=" + city + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        Log.e("lala", "" + MainActivity.city);
        call = apiService.getWeatherData(url);
        call.clone().enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.e("WeatherViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }
}
