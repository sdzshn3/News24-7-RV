package com.sdzshn3.android.news247.Retrofit;

import com.sdzshn3.android.news247.Retrofit.Weather.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<NewsModel> getResponse(@Url String url);

    @GET("top-headlines")
    Call<NewsModel> getNewsByCountry(
            @Query("country") String countryCode,
            @Query("apiKey") String apiKey,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("top-headlines")
    Call<NewsModel> getNewsByCategory(
            @Query("country") String countryCode,
            @Query("category") String category,
            @Query("apiKey") String apiKey,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET
    Call<WeatherModel> getWeatherData(@Url String url);
}
