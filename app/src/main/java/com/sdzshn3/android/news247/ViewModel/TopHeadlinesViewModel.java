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
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.Retrofit.NewsModel;
import com.sdzshn3.android.news247.Retrofit.Client;

import java.util.List;

public class TopHeadlinesViewModel extends AndroidViewModel {
    private static MutableLiveData<List<Article>> data = new MutableLiveData<>();
    private static Call<NewsModel> call;
    private ApiService apiService;

    public TopHeadlinesViewModel(Application application) {
        super(application);
        apiService = Client.getApiService();
        call = apiService.getResponse(TopHeadlinesFragment.URL);
        loadData();
    }

    private void loadData() {
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body().getArticles());
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Log.e("TopHeadlinesViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public void refresh() {
        call.clone().enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body().getArticles());
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Log.e("TopHeadlinesViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public LiveData<List<Article>> getData() {
        return data;
    }
}
