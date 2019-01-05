package com.sdzshn3.android.news247.ViewModel;

import com.sdzshn3.android.news247.Paging.NewsDataSource;
import com.sdzshn3.android.news247.Paging.NewsDataSourceFactory;
import com.sdzshn3.android.news247.Retrofit.Article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

public class ArticleViewModel extends ViewModel {

    public LiveData<PagedList<Article>>  articlePagedList;
    LiveData<PageKeyedDataSource<Integer, Article>> liveDataSource;

    public ArticleViewModel(String category) {

        NewsDataSourceFactory dataSourceFactory = new NewsDataSourceFactory(category);
        liveDataSource = dataSourceFactory.getArticleLiveDataSource();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(NewsDataSource.PAGE_SIZE)
                .build();

        articlePagedList = new LivePagedListBuilder(dataSourceFactory, config).build();

    }
}
