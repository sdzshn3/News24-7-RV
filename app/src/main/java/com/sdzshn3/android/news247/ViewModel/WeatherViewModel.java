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
import com.sdzshn3.android.news247.Repositories.WeatherRepository;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Weather.WeatherClient;
import com.sdzshn3.android.news247.Retrofit.Weather.WeatherModel;

public class WeatherViewModel extends AndroidViewModel {

    private WeatherRepository repository;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        repository = new WeatherRepository();
    }

    public LiveData<WeatherModel> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
