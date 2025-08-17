package com.example.storyapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insetAllStory(listStory : List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getAllStories() : PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    fun deleteAllStories()

}