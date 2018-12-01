package com.sdzshn3.android.news247.Retrofit.Weather;

import com.sdzshn3.android.news247.Retrofit.ApiService;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(DataHolder.WEATHER_REQUEST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
