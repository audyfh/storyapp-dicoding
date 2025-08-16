package com.example.storyapp.data.network.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.network.api.ApiService
import com.example.storyapp.data.network.model.Story
import com.example.storyapp.util.PreferencesManager
import retrofit2.HttpException
import java.io.IOException


class StoryPagingSource(
    private val apiService: ApiService,
    private val context: Context
) : PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
         try {
            val token = PreferencesManager.getToken(context) ?: ""
            val auth = "Bearer $token"
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getAllStory(
                token = auth,
                page = position,
                size = 5
            )
             Log.d("PagingSource",auth)
            if (responseData.isSuccessful){
                val listStory = responseData.body()?.listStory
                Log.d("PagingSource",listStory?.size.toString())
                return LoadResult.Page(
                    data = listStory ?: emptyList() ,
                    nextKey = if (listStory.isNullOrEmpty()) null else position +1,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1
                )
            } else {
                Log.d("PagingSource",responseData.errorBody().toString())
                return LoadResult.Error(Exception(responseData.message()))
            }


        } catch (exception: IOException) {
             return LoadResult.Error(exception)
         } catch (exception: HttpException) {
             return LoadResult.Error(exception)
         } catch (exception: Exception) {
             return LoadResult.Error(exception)
         }
    }
}