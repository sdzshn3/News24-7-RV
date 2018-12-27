package com.sdzshn3.android.news247.Room;

import com.sdzshn3.android.news247.TeluguNewsModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface TeluguNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addTeluguNews(List<TeluguNewsModel> teluguNews);

    @Query("SELECT * FROM telugu_news")
    LiveData<List<TeluguNewsModel>> getAllTeluguNews();

    @Query("DELETE FROM telugu_news")
    void nukeTable();

}
