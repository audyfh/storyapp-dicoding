package com.example.storyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.storyapp.data.local.dao.RemoteKeysDao
import com.example.storyapp.data.local.dao.StoryDao
import com.example.storyapp.data.local.entity.RemoteKeys
import com.example.storyapp.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeys::class],
    version = 1
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao() : StoryDao
    abstract fun remoteKeysDao() : RemoteKeysDao
}