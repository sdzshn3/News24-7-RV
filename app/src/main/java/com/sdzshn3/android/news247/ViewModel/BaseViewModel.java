package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.BaseRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class BaseViewModel extends AndroidViewModel {

    private BaseRepository repository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        repository = new BaseRepository();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
