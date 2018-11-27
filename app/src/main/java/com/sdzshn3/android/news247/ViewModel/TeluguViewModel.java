package com.sdzshn3.android.news247.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.News;
import com.sdzshn3.android.news247.QueryUtils;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;

import java.util.List;

public class TeluguViewModel extends AndroidViewModel {
    private static MutableLiveData<List<News>> data = new MutableLiveData<>();

    public TeluguViewModel(@NonNull Application application) {
        super(application);
        loadData();
    }

    public static void loadData() {
        new AsyncTask<Void, Void, List<News>>() {
            @Override
            protected List<News> doInBackground(Void... voids) {
                QueryUtils queryUtils = new QueryUtils();
                try {
                    return queryUtils.fetchNewsData(DataHolder.TELUGU_NEWS_LOADER_ID, DataHolder.TELUGU_NEWS_REQUEST_URL, Integer.parseInt(TeluguNewsFragment.numberOfArticles));
                } catch (NumberFormatException e) {
                    Log.e("TeluguViewModel", "numberOfArticles not initialised");
                    return null;
                }

            }

            @Override
            protected void onPostExecute(List<News> newsList) {
                data.postValue(newsList);
            }
        }.execute();
    }

    public LiveData<List<News>> getData() {
        return data;
    }
}
