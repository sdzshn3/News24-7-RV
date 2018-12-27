package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.Repositories.TeluguRepository;
import com.sdzshn3.android.news247.TeluguNewsModel;
import com.sdzshn3.android.news247.QueryUtils;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;

import java.util.List;

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
