package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.ScienceRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ScienceViewModel extends AndroidViewModel {
    private ScienceRepository repository;

    public ScienceViewModel(@NonNull Application application) {
        super(application);
        repository = new ScienceRepository();
    }

    public void refresh() {
        repository.refresh();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }
}
