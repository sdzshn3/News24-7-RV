package com.sdzshn3.android.news247.Paging;

import android.text.TextUtils;
import android.util.Log;

import com.sdzshn3.android.news247.BuildConfig;
import com.sdzshn3.android.news247.Retrofit.Article;
import com.sdzshn3.android.news247.Retrofit.Client;
import com.sdzshn3.android.news247.Retrofit.NewsModel;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Response;

public class NewsDataSource extends PageKeyedDataSource<Integer, Article> {

    private static final String COUNTRY_CODE = "in";
    private static final String API_KEY = BuildConfig.NEWS_API_KEY;
    private static final int FIRST_PAGE = 1;
    public static final int PAGE_SIZE = 20;

    private String category;
    public static final String TAG = NewsDataSource.class.getSimpleName();

    public NewsDataSource(String category) {
        this.category = category;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Article> callback) {

        try {
            Response<NewsModel> response;
            if (category == null || TextUtils.isEmpty(category)) {
                response = Client.getApiService().getNewsByCountry(COUNTRY_CODE, API_KEY, FIRST_PAGE, PAGE_SIZE)
                        .execute();

            } else {
                response = Client.getApiService().getNewsByCategory(COUNTRY_CODE, category, API_KEY, FIRST_PAGE, PAGE_SIZE)
                        .execute();
            }
            Log.d(TAG, "Initial callback");
            if (response.body() != null && response.body().getArticles() != null) {
                Log.d(TAG, "Initial callback num of articles : " + response.body().getArticles().size());
                callback.onResult(response.body().getArticles(), null,
                        FIRST_PAGE + 1);
            }
        } catch (IOException e) {
            Log.d(TAG, "Initial callback failure -> " + e.getMessage());
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Article> callback) {

        try {
            Response<NewsModel> response;
            if (category == null || TextUtils.isEmpty(category)) {
                response = Client.getApiService().getNewsByCountry(COUNTRY_CODE, API_KEY, params.key, PAGE_SIZE)
                        .execute();

            } else {
                response = Client.getApiService().getNewsByCategory(COUNTRY_CODE, category, API_KEY, params.key, PAGE_SIZE)
                        .execute();
            }
            if (response.body() != null && response.body().getArticles() != null) {
                Log.d(TAG, "Before callback num of articles : " + response.body().getArticles().size());
                Integer key = params.key > 1 ? params.key - 1 : null;
                callback.onResult(response.body().getArticles(), key);
            }
        } catch (IOException e) {
            Log.d(TAG, "Before callback failure -> " + e.getMessage());
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Article> callback) {

        try {
            Response<NewsModel> response;
            if (category == null || TextUtils.isEmpty(category)) {
                response = Client.getApiService().getNewsByCountry(COUNTRY_CODE, API_KEY, params.key, PAGE_SIZE)
                        .execute();

            } else {
                response = Client.getApiService().getNewsByCategory(COUNTRY_CODE, category, API_KEY, params.key, PAGE_SIZE)
                        .execute();
            }
            if (response.body() != null && response.body().getArticles() != null) {
                Log.d(TAG, "After callback num of articles : " + response.body().getArticles().size());
                Integer key = response.body().getStatus().equals("ok") ? params.key + 1 : null;
                callback.onResult(response.body().getArticles(), key);
            }
        } catch (IOException e) {
            Log.d(TAG, "After callback failure -> " + e.getMessage());
        }
    }
}
