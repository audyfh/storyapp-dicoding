package com.example.storyapp.data.network.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.network.api.ApiService
import com.example.storyapp.data.network.model.LoginRequest
import com.example.storyapp.data.network.model.LoginResult
import com.example.storyapp.data.network.model.RegisterRequest
import com.example.storyapp.data.network.model.Story
import com.example.storyapp.util.PreferencesManager
import com.example.storyapp.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    @ApplicationContext private val context: Context
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory(): LiveData<PagingData<StoryEntity>> {
       return Pager(
           config = PagingConfig(
               pageSize = 5
           ),
           remoteMediator = StoryRemoteMediator(
               apiService = apiService,
               context = context,
               database = storyDatabase
           ),
           pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
           }
       ).liveData
    }

    fun getDetailStory(id: String): Flow<Resource<Story>> {
        val token = PreferencesManager.getToken(context) ?: ""
        val auth = "Bearer $token"
        return flow {
            emit(Resource.Loading())
            try {
                val data = apiService.getDetailStory(
                    token = auth,
                    id = id
                )
                if (data.isSuccessful) {
                    if (data.body()?.story != null) {
                        emit(Resource.Success(data.body()?.story))
                    } else {
                        emit(Resource.Error(data.body()?.message))
                    }
                } else {
                    emit(Resource.Error(data.message()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    }

    fun login(
        email: String,
        password: String
    ): Flow<Resource<LoginResult>> {
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        return flow {
            emit(Resource.Loading())
            try {
                val data = apiService.login(request = loginRequest)
                if (data.isSuccessful) {
                    val token = data.body()?.loginResult?.token
                    if (token != null) {
                        PreferencesManager.saveToken(context, token)
                    }
                    emit(Resource.Success(data.body()?.loginResult))
                } else {
                    emit(Resource.Error(msg = data.body()?.message))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<String>> {
        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password
        )
        return flow {
            emit(Resource.Loading())
            try {
                val data = apiService.register(registerRequest)
                if (data.isSuccessful) {
                    emit(Resource.Success(data.message()))
                } else {
                    emit(Resource.Error(data.message()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    }

    fun uploadStory(
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ): Flow<Resource<String>> {
        val token = PreferencesManager.getToken(context) ?: ""
        val auth = "Bearer $token"
        val descPart = description.toRequestBody("text/plain".toMediaType())
        val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData(
            name = "photo",
            filename = imageFile.name,
            body = imageRequestBody
        )
        val latPart = lat?.toString()?.toRequestBody("text/plain".toMediaType())
        val lonPart = lon?.toString()?.toRequestBody("text/plain".toMediaType())

        return flow {
            emit(Resource.Loading())
            try {
                val data = apiService.addStory(
                    token = auth,
                    description = descPart,
                    lat = latPart,
                    lon = lonPart,
                    photo = imagePart
                )

                if (data.isSuccessful) {
                    emit(Resource.Success(data.message()))
                } else {
                    emit(Resource.Error(data.message()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    }
}