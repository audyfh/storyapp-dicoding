package com.example.storyapp.data.network.api

import com.example.storyapp.data.network.model.ApiResponse
import com.example.storyapp.data.network.model.DetailStoryResponse
import com.example.storyapp.data.network.model.ListStoryResponse
import com.example.storyapp.data.network.model.LoginRequest
import com.example.storyapp.data.network.model.LoginResponse
import com.example.storyapp.data.network.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("stories")
    suspend fun getAllStory(
        @Header("authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ListStoryResponse>

    @GET("stories")
    suspend fun getAllStoryWithMaps(
        @Header("authorization") token: String,
        @Query("location") location : Int = 1
    ): Response<ListStoryResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("authorization") token: String,
        @Path("id") id: String
    ): Response<DetailStoryResponse>

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<ApiResponse>

    companion object {
         var BASE_URL = "https://story-api.dicoding.dev/v1/"
    }
}