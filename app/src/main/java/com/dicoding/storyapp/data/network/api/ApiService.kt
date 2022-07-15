package com.dicoding.storyapp.data.network.api

import com.dicoding.storyapp.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") auth: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<UploadStoryResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int = 0,
    ): UserStoryResponse

    @GET("stories")
    fun getLocationStories(
        @Header("Authorization") auth: String,
        @Query("size") size: Int = 25,
        @Query("location") location: Int = 1,
    ): Call<UserStoryResponse>
}