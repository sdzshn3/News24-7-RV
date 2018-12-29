package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.SportsRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SportsViewModel extends AndroidViewModel {

    private SportsRepository repository;

    public SportsViewModel(@NonNull Application application) {
        super(application);
        repository = new SportsRepository();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
