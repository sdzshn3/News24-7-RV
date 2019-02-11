package com.sdzshn3.android.news247.Paging;

import com.sdzshn3.android.news247.Retrofit.Article;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class NewsDataSourceFactory extends DataSource.Factory<Integer, Article> {

    private MutableLiveData<PageKeyedDataSource<Integer, Article>> articleLiveDataSource = new MutableLiveData<>();
    private String category;

    public NewsDataSourceFactory(String category) {
        this.category = category;
    }

    @Override
    public DataSource<Integer, Article> create() {
        NewsDataSource dataSource = new NewsDataSource(category);
        articleLiveDataSource.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Article>> getArticleLiveDataSource() {
        return articleLiveDataSource;
    }
}
