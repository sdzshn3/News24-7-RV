package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sdzshn3.android.news247.Fragments.TopHeadlinesFragment;
import com.sdzshn3.android.news247.Repositories.TopHeadlinesRepository;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.Retrofit.NewsModel;
import com.sdzshn3.android.news247.Retrofit.Client;

import java.util.List;

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
