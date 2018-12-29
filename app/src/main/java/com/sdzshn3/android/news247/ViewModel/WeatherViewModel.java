package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.WeatherRepository;
import com.sdzshn3.android.news247.Retrofit.Weather.WeatherModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
