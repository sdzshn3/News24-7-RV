package com.sdzshn3.android.news247.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<NewsModel> getResponse(@Url String url);
}
