package com.sdzshn3.android.news247;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.Room.AppDatabase;

public class MainApp extends Application {

    public AppDatabase getAppDatabase(){
        return AppDatabase.getDatabase(this);
    }

    public QueryUtils getQueryUtils(){
        return new QueryUtils();
    }

    public TeluguRepository getTeluguRepository(){
        return TeluguRepository.getInstance(getAppDatabase(), getQueryUtils());
    }

}
