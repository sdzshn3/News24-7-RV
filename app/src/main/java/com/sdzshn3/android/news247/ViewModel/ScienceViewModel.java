package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sdzshn3.android.news247.Fragments.ScienceNewsFragment;
import com.sdzshn3.android.news247.Repositories.ScienceRepository;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.Retrofit.Client;
import com.sdzshn3.android.news247.Retrofit.NewsModel;

import java.util.List;

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
