package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import android.util.Log;

import com.sdzshn3.android.news247.Fragments.HealthNewsFragment;
import com.sdzshn3.android.news247.Repositories.HealthRepository;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.Retrofit.Client;
import com.sdzshn3.android.news247.Retrofit.NewsModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthViewModel extends AndroidViewModel {

    private HealthRepository repository;

    public HealthViewModel(@NonNull Application application) {
        super(application);
        repository = new HealthRepository();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
