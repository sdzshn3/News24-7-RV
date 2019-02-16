package com.sdzshn3.android.news247.Repositories;

import android.os.AsyncTask;
import android.util.Log;

import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.QueryUtils;
import com.sdzshn3.android.news247.Room.AppDatabase;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;
import com.sdzshn3.android.news247.TeluguNewsModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TeluguRepository {

    private static TeluguRepository repository;

    private final AppDatabase mAppDatabase;

    private final QueryUtils mQueryUtils;

    public TeluguRepository(final AppDatabase database, QueryUtils queryUtils){

        mAppDatabase = database;
        mQueryUtils = queryUtils;

    }

    public static synchronized TeluguRepository getInstance(final AppDatabase database, final QueryUtils queryUtils){

        if(repository == null){
            repository = new TeluguRepository(database, queryUtils);
        }

        return repository;

    }

    public LiveData<List<TeluguNewsModel>> getAllTeluguNews(){
        fetchLatestNews();
        return mAppDatabase.teluguNewsDao().getAllTeluguNews();
    }


    public void fetchLatestNews() {

        AsyncTask.execute(() -> {
            List<TeluguNewsModel> newsModelsList = mQueryUtils.extractTeluguNewsFromRss(DataHolder.TELUGU_NEWS_REQUEST_URL, Integer.parseInt(TeluguNewsFragment.numberOfArticles));
            insertNews(newsModelsList);
        });
    }

    private void insertNews(List<TeluguNewsModel> newsList) {
        AsyncTask.execute(() -> {
            mAppDatabase.teluguNewsDao().nukeTable();
            mAppDatabase.teluguNewsDao().addTeluguNews(newsList);
        });
    }
}
