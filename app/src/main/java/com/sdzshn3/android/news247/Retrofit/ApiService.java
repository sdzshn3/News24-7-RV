package com.sdzshn3.android.news247.Retrofit;

import com.sdzshn3.android.news247.Retrofit.Weather.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<NewsModel> getResponse(@Url String url);

    @GET
    Call<WeatherModel> getWeatherData(@Url String url);
}
