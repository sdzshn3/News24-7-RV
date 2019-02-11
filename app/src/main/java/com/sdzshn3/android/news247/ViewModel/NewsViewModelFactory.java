package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private String category;

    public NewsViewModelFactory(Application application, String category) {
        this.application = application;
        this.category = category;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(category);
    }
}
