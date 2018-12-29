package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.HealthRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
