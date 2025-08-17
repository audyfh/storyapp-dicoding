package com.example.storyapp.di

import android.content.Context
import androidx.room.Room
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.network.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideApiService() : ApiService {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideStoryDatabse(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        StoryDatabase::class.java,
        "story_db"
    ).build()
}