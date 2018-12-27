package com.sdzshn3.android.news247.Repositories;

import android.os.AsyncTask;
import android.util.Log;

import com.sdzshn3.android.news247.Fragments.TeluguNewsFragment;
import com.sdzshn3.android.news247.QueryUtils;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;
import com.sdzshn3.android.news247.TeluguNewsModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TeluguRepository {
    private static MutableLiveData<List<TeluguNewsModel>> data = new MutableLiveData<>();

    public TeluguRepository(){
        loadData();
    }

    public static void loadData() {
        new AsyncTask<Void, Void, List<TeluguNewsModel>>() {
            @Override
            protected List<TeluguNewsModel> doInBackground(Void... voids) {
                QueryUtils queryUtils = new QueryUtils();
                try {
                    return queryUtils.extractTeluguNewsFromRss(DataHolder.TELUGU_NEWS_REQUEST_URL, Integer.parseInt(TeluguNewsFragment.numberOfArticles));
                } catch (NumberFormatException e) {
                    Log.e("TeluguRepository", "numberOfArticles not initialised");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<TeluguNewsModel> newsList) {
                data.postValue(newsList);
            }
        }.execute();
    }

    public LiveData<List<TeluguNewsModel>> getData() {
        return data;
    }
}
