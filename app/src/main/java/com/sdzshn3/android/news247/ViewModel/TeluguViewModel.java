package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.MainApp;
import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.TeluguNewsModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TeluguViewModel extends AndroidViewModel {
    private TeluguRepository repository;

    private MediatorLiveData<List<TeluguNewsModel>> mObservableTeluguNews;

    public TeluguViewModel(@NonNull Application application, TeluguRepository repository) {
        super(application);
        this.repository = repository;
        mObservableTeluguNews = new MediatorLiveData<>();
    }

    public MediatorLiveData<List<TeluguNewsModel>> getmObservableTeluguNewsData() {
        mObservableTeluguNews.addSource(repository.getAllTeluguNews(), teluguNewsModels -> mObservableTeluguNews.setValue(teluguNewsModels));
        return mObservableTeluguNews;
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory{
        private TeluguRepository mTeluguRepository;
        private Application mApplication;

        public Factory(Application application){
            super(application);
            mApplication = application;
            mTeluguRepository = ((MainApp)mApplication).getTeluguRepository();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TeluguViewModel(mApplication, mTeluguRepository);
        }
    }
}
