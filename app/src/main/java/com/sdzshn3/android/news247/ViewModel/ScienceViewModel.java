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
import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.Retrofit.Client;
import com.sdzshn3.android.news247.Retrofit.NewsModel;
import com.sdzshn3.android.news247.Retrofit.Results;

import java.util.List;

public class ScienceViewModel extends AndroidViewModel {
    private static MutableLiveData<List<Results>> data = new MutableLiveData<>();
    private ApiService apiService;
    private static Call<NewsModel> call;

    public ScienceViewModel(@NonNull Application application) {
        super(application);
        apiService = Client.getApiService();
        call = apiService.getResponse(ScienceNewsFragment.URL);
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
                Log.e("ScienceViewModel", "onFailure", t);
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
                Log.e("ScienceViewModel", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public LiveData<List<Results>> getData() {
        return data;
    }
}
