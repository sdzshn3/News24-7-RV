package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.TopHeadlinesRepository;
import com.sdzshn3.android.news247.Retrofit.Article;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TopHeadlinesViewModel extends AndroidViewModel {

    private TopHeadlinesRepository repository;

    public TopHeadlinesViewModel(Application application) {
        super(application);
        repository = new TopHeadlinesRepository();
    }

    public void refresh() {
        repository.refresh();
    }

    public LiveData<List<Article>> getData() {
        return repository.getData();
    }
}
