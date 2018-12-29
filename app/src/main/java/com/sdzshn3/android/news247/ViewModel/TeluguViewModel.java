package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;

import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.TeluguNewsModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TeluguViewModel extends AndroidViewModel {
    private TeluguRepository repository;

    public TeluguViewModel(@NonNull Application application) {
        super(application);
        repository = new TeluguRepository();
    }

    public LiveData<List<TeluguNewsModel>> getData() {
        return repository.getData();
    }
}
