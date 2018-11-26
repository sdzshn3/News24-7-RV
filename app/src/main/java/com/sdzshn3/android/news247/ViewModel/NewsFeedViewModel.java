package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sdzshn3.android.news247.Fragments.NewsFeedFragment;
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.NewsModel;
import com.sdzshn3.android.news247.Retrofit.Client;
import com.sdzshn3.android.news247.Retrofit.Results;

import java.util.List;

public class NewsFeedViewModel extends AndroidViewModel {
    private static MutableLiveData<List<Results>> data = new MutableLiveData<>();
    private static Call<NewsModel> call;
    private ApiService apiService;

    public NewsFeedViewModel(Application application) {
        super(application);
        apiService = Client.getApiService();
        call = apiService.getResponse(NewsFeedFragment.URL);
        loadData();
    }

    private void loadData() {
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body().getResponse().getResults());
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Log.e("NewsFeedViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public void refresh() {
        call.clone().enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body().getResponse().getResults());
                }
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                Log.e("NewsFeedViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public LiveData<List<Results>> getData() {
        return data;
    }
}
