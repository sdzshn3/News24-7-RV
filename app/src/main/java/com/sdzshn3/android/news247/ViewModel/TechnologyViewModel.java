package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.TechnologyRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TechnologyViewModel extends AndroidViewModel {

    private TechnologyRepository repository;

    public TechnologyViewModel(@NonNull Application application) {
        super(application);
        repository = new TechnologyRepository();
    }

    public void refresh() {
        repository.refresh();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }
}
